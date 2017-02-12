package oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;

import selenium.SeleniumTest;


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
	
	private StringBuilder buildResult;
	private StringBuilder imageSize;
	private volatile boolean isDone = false;
	private static final String TEST_FILE = "./test.sh";
	private static final String DOCKER_TEST_FILE = "./testDocker.sh";
	private final String database;
	
	private final String GECKODRIVER_PATH = System.getProperty("user.dir") + "/geckodriver";
	
	public ThreadCheckBuild(String path, boolean useDocker, String logFile, StringBuilder imageSize, StringBuilder buildResult, String database){
		this.PATH = path;
		this.USE_DOCKER = useDocker;
		this.LOG_FILE = logFile;
		this.imageSize = imageSize;
		this.buildResult = buildResult;
		this.database = database;
		isDone = false;
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
				Thread.sleep(2*1000);
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
				
				if(database.equals("\"mysql\"") || database.equals("\"postgresql\"") 
				|| database.equals("\"mongodb\"") || database.equals("\"cassandra\"")){
					_log.info("Starting to populate the database");
					SeleniumTest selenium = new SeleniumTest(GECKODRIVER_PATH);
					selenium.populateDB(database);
					_log.info("Done");
				}
				_log.info("Running some tests...");
				buildResult.delete(0,5);
				buildResult.append("OK");
				// if success: run Tests
				if (!USE_DOCKER) startProcess(TEST_FILE);
				else startProcess(DOCKER_TEST_FILE);
				//Extract docker images size
				if (USE_DOCKER) imageSize.append(extractDockerImageSize("jhipster"));
				_log.info("All done ! Killing the server...");
				// Then kill server
				killServer();
				isDone = true;
			}
			
			if(buildFailed){
				_log.info("Build failed... Killing the server now...");
				buildResult.delete(0, 5);
				buildResult.append("KO");
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
		Matcher m4 = Pattern.compile("((.*?)exited with code)").matcher(logs);
		Matcher m5 = Pattern.compile("((.*?)bind: address already in use)").matcher(logs);
		Matcher m6 = Pattern.compile("((.*?)startup failed)").matcher(logs);
		Matcher m7 = Pattern.compile("((.*?)Error parsing reference:)").matcher(logs);
		
		while(m7.find() | m6.find() | m4.find() | m5.find() | m.find() | m2.find() | m3.find()) return true;
		return false;
	}
	
	
	private static void runCommand(String command,String[] env, File path){
		try{
			Process p = Runtime.getRuntime().exec(command,env,path);
			p.waitFor();
			_log.info("Done running command");
		} catch (Exception e) { 
			_log.error("An error occured");
			_log.error(e.getMessage());}
	}
	
	private void killServer(){
		// TODO Docker => kill server in Oracle.java
		/*if (USE_DOCKER) startProcess("./dockerStop.sh");
		else startProcess("./killScript.sh");*/
		if(!USE_DOCKER) startProcess("./killScript.sh");
		else runCommand("docker-compose -f src/main/docker/app.yml stop",null,new File(PATH));
		// TODO docker -> docker-compose -f src/main/docker/app.yml stop
	}
	
	
	private String extractDockerImageSize(String image){
		String result = "ND";
		try {
			Process process = Runtime.getRuntime().exec("docker images "+image+" --format \"{{.Size}}\"");
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        result = inputLine;
			    }
			    in.close();
		} catch (IOException e) {
			_log.error("IOException: "+e.getMessage());
		}
		return result;
	}
}