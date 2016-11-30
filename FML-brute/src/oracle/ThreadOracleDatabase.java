package oracle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Thread to handle the deploymnent of the image Docker of the oracle database
 * 
 * @author Nuttinck Alexandre
 */
public class ThreadOracleDatabase implements Runnable {
	private static final Logger _log = Logger.getLogger("ThreadOracleDatabsase");
	private Process process;
	private String PATH;
	
	public ThreadOracleDatabase(String path){
		this.PATH = path;
	}
	
	public void stop(){
		this.process.destroy();
	}
	
	/**
	 * Create a process to run the Database Oracle
	 */
	@Override
	public void run(){
		try{
			ProcessBuilder processBuilder = new ProcessBuilder("./oracleDatabaseInit.sh");
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
