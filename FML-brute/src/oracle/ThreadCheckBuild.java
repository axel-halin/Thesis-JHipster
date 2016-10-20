package oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;

//TODO can use ResultChecker to get PATH


/**
 * Thread to check if the build of the application is successful.
 * To do so, it reads periodically the log file in search of a specific pattern.
 * 
 * @author Axel Halin
 */
public class ThreadCheckBuild extends Thread {
	
	private static final Logger _log = Logger.getLogger("ThreadCheckBuild");
	
	private final String PATH;			// Path to the current App
	private final boolean USE_DOCKER;
	private final String LOG_FILE;// buildDocker.log
	
	private StringBuilder imageSize;
	private static volatile boolean isDone = false;
	private static final String TEST_FILE = "./test.sh";
	
	
	public ThreadCheckBuild(String path, boolean useDocker, String logFile, StringBuilder imageSize){
		this.PATH = path;
		this.USE_DOCKER = useDocker;
		this.LOG_FILE = logFile;
		this.imageSize = imageSize;
	}
	
	public void done(){
		isDone = true;
	}
	
	
	/**
	 * Start a process to run the test included in TEST_FILE.
	 */
	private void startProcess(String file){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(file);
			processBuilder.directory(new File(PATH));
			process = processBuilder.start();
			process.waitFor();
		} catch(IOException e){
			_log.error("IOException: "+e.getMessage());
		} catch(InterruptedException e){
			_log.error("InterruptedException: "+e.getMessage());
		} finally{
			try{process.destroy();}
			catch(Exception e){_log.error("Destroy error: "+e.getMessage());}
		}
	}
	

	@Override
	public void run(){
		String logs = "";
		boolean buildSuccess = false;
		boolean buildFailed = false;
		while(!isDone)
		{
			try{
				Thread.sleep(15*1000);
			} catch (InterruptedException e){
				_log.error("InterruptedException: "+e.getMessage());
			}
			
			// Read Log File (handle file not found exception)
			try{
				logs = Files.readFileIntoString(PATH+LOG_FILE);
				// Check if build success or failure
				buildSuccess = checkBuildSuccess(logs);
				if (!buildSuccess) buildFailed = checkBuildFailure(logs);
			} catch (Exception e){
				_log.error("Exeception: "+e.getMessage());
			}
			
			if(buildSuccess){
				_log.info("Build successful ! Trying to run tests...");
				// if success: run Tests
				startProcess(TEST_FILE);
				//Extract docker images size
				if (USE_DOCKER) imageSize.append(extractDockerImageSize("jhipster"));
				_log.info("All done ! Killing the server...");
				// Then kill server
				killServer();
				isDone = true;
			}
			
			if(buildFailed){
				_log.info("Build failed... Killing the server now...");
				// Kill
				killServer();
				isDone = true;
			}
		}
	}

	
	private boolean checkBuildSuccess(String logs){
		Matcher m = Pattern.compile("((.*?)Application 'jhipster' is running!)").matcher(logs);
		while (m.find()) return true;
		return false;
	}
		
	
	private boolean checkBuildFailure(String logs){
		Matcher m = Pattern.compile("((.*?)APPLICATION FAILED TO START)").matcher(logs);
		Matcher m2 = Pattern.compile("((.*?)BUILD FAILED)").matcher(logs);
		Matcher m3 = Pattern.compile("((.*?)BUILD FAILURE)").matcher(logs);
		Matcher m4 = Pattern.compile("((.*?)docker_jhipster-app_1 exited with code)").matcher(logs);
		Matcher m5 = Pattern.compile("((.*?)bind: address already in use)").matcher(logs);
		
		while(m4.find() | m5.find() | m.find() | m2.find() | m3.find()) return true;
		return false;
	}
	
	private void killServer(){
		if (USE_DOCKER) startProcess("./dockerStop.sh");
		else startProcess("killScript.sh");
	}
	
	
	private String extractDockerImageSize(String image){
		String result = "";
		try {
			Process process = Runtime.getRuntime().exec("docker images "+image+" --format \"{{.Size}}\"");
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        result += inputLine;
			    }
			    in.close();
		} catch (IOException e) {
			_log.error("IOException: "+e.getMessage());
		}
		return result;
	}
}
