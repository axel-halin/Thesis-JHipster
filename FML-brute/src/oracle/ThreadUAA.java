package oracle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ThreadUAA implements Runnable{
	private final Logger _log = Logger.getLogger("ThreadUAA");
	private final Boolean SYSTEM;
	private final String PATH;
	
	public ThreadUAA(Boolean system, String path){
		this.SYSTEM = system;
		this.PATH = path;
	}
	
	public void run(){
		Process process = null;
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