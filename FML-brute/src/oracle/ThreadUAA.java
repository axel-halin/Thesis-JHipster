package oracle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Handle the deployment of UAA Server.
 * 
 * @author Axel Halin
 */
public class ThreadUAA implements Runnable{
	private final Logger _log = Logger.getLogger("ThreadUAA");
	private final String PATH;
	
	public ThreadUAA(String path){
		this.PATH = path;
	}
	
	public void run(){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder("./build.sh");
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
}