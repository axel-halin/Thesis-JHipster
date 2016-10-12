package oracle;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ThreadDeploy implements Runnable{
	private static final Logger _log = Logger.getLogger("ThreadDeploy");
	private final Boolean system;
	private final String fileName;
	private final String directory;
	
	public ThreadDeploy(Boolean system, String fileName, String directory){
		this.system = system;
		this.fileName = fileName;
		this.directory = directory;
	}
	
	@Override
	public void run(){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			if(system) processBuilder.directory(new File(directory));
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

}
