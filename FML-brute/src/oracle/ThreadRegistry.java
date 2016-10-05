package oracle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Thread to handle JHipster Registry
 * 
 * @author Axel Halin
 */
public class ThreadRegistry implements Runnable {
	private static final Logger _log = Logger.getLogger("ThreadRegistry");
	private final Boolean SYSTEM;
	private final String PATH;
	private Process process;
	
	public ThreadRegistry(Boolean system, String path){
		this.SYSTEM = system;
		this.PATH = path;
	}
	
	public void stop(){
		this.process.destroy();
	}
	
	@Override
	public void run(){
		try{
			ProcessBuilder processBuilder = new ProcessBuilder("./mvnw");
			if(SYSTEM) processBuilder.directory(new File(PATH));
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
