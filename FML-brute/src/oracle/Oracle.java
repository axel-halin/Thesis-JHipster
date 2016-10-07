package oracle;

import csv.CSVUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Oracle for all variants of configurator JHipster
 * 	- generate 
 * 	- build
 *  - tests
 * 
 * @author Nuttinck Alexandre
 * @author Axel Halin
 *
 */
public class Oracle {

	private static final Logger _log = Logger.getLogger("Oracle");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final Integer weightFolder = new File(JHIPSTERS_DIRECTORY+"/").list().length;

	private Thread threadRegistry;
	private Thread threadUAA;

	private String projectDirectory = System.getProperty("user.dir");

	/**
	 * @param nameScriptBat Name of the Script .bat
	 * @param nameScriptSh Name of the Sript .sh
	 * @param jDirectory Name of the folder
	 */
	private void writeScriptBat(String nameScriptBat,String nameScriptSh,String jDirectory)
	{
		if (nameScriptBat != "bashgitlaunchDatabases.bat"){
			String buildScript = "cd "+getjDirectory(jDirectory)+"\n"; 
			buildScript += "\"C:/Program Files/Git/bin/sh.exe\" --login ./" + nameScriptSh;
			Files.writeStringIntoFile(getjDirectory(jDirectory) + nameScriptBat, buildScript);
		} else{
			String buildScript =  "\"C:/Program Files/Git/bin/sh.exe\" --login ./" + nameScriptSh;
			Files.writeStringIntoFile("bashgitlaunchDatabases.bat", buildScript);
		}
	}

	private void startProcess(String fileName, boolean system, String desiredDirectory){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			if(system) processBuilder.directory(new File(projectDirectory + "/" + desiredDirectory));
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

	private void startProcess(String fileName,boolean system, String jDirectory, long timeOut, TimeUnit unit){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			if(system) processBuilder.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
			process = processBuilder.start();

			if(!process.waitFor(timeOut, unit)) {
			    //timeout - kill the process. 
			    process.destroyForcibly();
			}	
		} catch(IOException e){
			_log.error("IOException: "+e.getMessage());
		} catch(InterruptedException e){
			_log.error("InterruptedException: "+e.getMessage());
		} finally{
			try{process.destroyForcibly(); process.destroy();}
			catch(Exception e){_log.error("Destroy error: "+e.getMessage());}
		}
	}

	/**
	 * Generate the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void generateApp(String jDirectory,boolean system) throws InterruptedException, IOException{
		// for windows add script bashgit.bat launch bashgit and execute generate.sh
		if (!system) startProcess(getjDirectory(jDirectory) + "bashgitgenerate.bat", system, jDirectory);
		else startProcess("./generate.sh",system,JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
	}

	/**
	 * Check the App is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkGenerateApp(String jDirectory) throws FileNotFoundException{

		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "generate.log");

		//CHECK IF Server app generated successfully.
		//OR Client app generated successfully.
		Matcher m = Pattern.compile("((.*?)Server app generated successfully.)").matcher(text);
		Matcher m2 = Pattern.compile("((.*?)Client app generated successfully.)").matcher(text);

		while(m.find() | m2.find()) return true; 
		return false;
	}

	/**
	 * Compile the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 */
	private void compileApp(String jDirectory, boolean system){

		// for windows add script bashgit.bat launch bashgit and execute generate.sh
		if (!system) {startProcess(getjDirectory(jDirectory) + "bashgitcompile.bat", system, jDirectory);}
		else startProcess("./compile.sh", system, JHIPSTERS_DIRECTORY+"/"+jDirectory);

	}



	/**
	 * Build the App which is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 */
	private void buildApp(String jDirectory, boolean system) throws InterruptedException{
		// for windows add script bashgit.bat launch bashgit and execute build.sh
		if (!system) startProcess(getjDirectory(jDirectory)+"bashgitbuild.bat", system, jDirectory, 200, TimeUnit.SECONDS);
		else startProcess("./build.sh", system, jDirectory, 150, TimeUnit.SECONDS);
		
		startProcess("./killScript.sh", system, JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}

	/**
	 * Check the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkBuildApp(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "build.log");

		//CHECK IF BUILD FAILED THEN false
		Matcher m = Pattern.compile("((.*?)APPLICATION FAILED TO START)").matcher(text);
		Matcher m2 = Pattern.compile("((.*?)BUILD FAILED)").matcher(text);
		Matcher m3 = Pattern.compile("((.*?)BUILD FAILURE)").matcher(text);

		while(m.find() | m2.find() | m3.find()) return false;
		return true;
	}

	/**
	 * Launch Tests on the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 */
	private void testGenerateApp(String jDirectory, boolean system) throws InterruptedException{
		if(!system) startProcess(getjDirectory(jDirectory)+"bashgittest.bat", system, jDirectory);
		else startProcess("./test.sh", system, jDirectory);
	}

	/**
	 * Return the path to folder jDirectory (which is in the relative path JHIPSTERS_DIRECTORY/)
	 * 
	 * @param jDirectory Name of the folder
	 * @return The relative path to folder with name jDirectory.
	 */
	private String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}

	/**
	 * Return the value of the system 
	 * 
	 * @return true if linux else false for windows
	 * @throws IOException 
	 */
	private boolean getValueOfSystem() {
		InputStream inputStream = null;

		try {
			Properties prop = new Properties();
			String propFileName = "System.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

			Boolean system = Boolean.parseBoolean(prop.getProperty("system"));

			return system;
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try{inputStream.close();}
			catch (IOException e){e.printStackTrace();}
		}
		return true; // default linux
	}

	/**
	 * Return stacktrace if the app doesn't compile or execute 
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of stacktraces
	 * 
	 */
	private String extractStacktraces(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "build.log");

		//m1 Exceptions
		Matcher m1 = Pattern.compile(".+Exception[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+").matcher(text);
		//m2 Exceptions not find in m1 regex
		Matcher m2 = Pattern.compile("(.*\\bException\\b.*)\\r?\\n(.*\\r?\\n)*(.*\\bat\\b.*)*(\\d{1,4}\\)\\r?\\n)").matcher(text);

		String stacktraces = "";

		while(m1.find() | m2.find())
		{
			stacktraces = stacktraces + m1.toString();
			stacktraces = stacktraces + m2.toString();
		}

		return stacktraces;
	}
	
	/**
	 * Return the time of building   
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractTimeBuild(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "build.log");

		//m1 TimeBuild TODO
		Matcher m1 = Pattern.compile("(.*?)Total time").matcher(text);

		String timebuild = "NOTHING";

		while(m1.find())
		{
			System.out.println(m1.toString());
			return timebuild = m1.toString();
		}

		return timebuild;
	}

	/**
	 * Return the time of building   
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractMemoryBuild(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "build.log");

		//m1 TimeBuild TODO
		Matcher m1 = Pattern.compile("(.*?)Final Memory").matcher(text);

		String memoryBuild = "NOTHING";

		while(m1.find())
		{
			System.out.println(m1.toString());
			return memoryBuild = m1.toString();
		}

		return memoryBuild;
	}


	/**
	 * Launch initialization scripts:\n
	 * 		- Start Uaa Server (in case of Uaa authentication)
	 * 		- Start Jhipster-Registry (in case of Microservices)
	 *  
	 * @param system Boolean to check OS (True = Linux, False = Windows)
	 */
	private void initialization(final Boolean system){
		_log.info("Starting intialization scripts...");
		
		// Start Jhipster Registry
		threadRegistry = new Thread(new ThreadRegistry(system, projectDirectory+"/JHipster-Registry/"));
		threadRegistry.start();

		// Let Jhipster Registry initiate before attempting to launch UAA Server...
		try{Thread.sleep(30000);}
		catch(Exception e){_log.error(e.getMessage());}
		
		// Start UAA Server
		threadUAA = new Thread(new ThreadUAA(system, projectDirectory+"/"+JHIPSTERS_DIRECTORY+"/uaa/"));
		threadUAA.start();
		
		try{Thread.sleep(5000);}
		catch(Exception e){_log.error(e.getMessage());}
		
		_log.info("Oracle intialized !");
	}


	private void termination(){
		threadRegistry.interrupt();
		threadUAA.interrupt();
	}

	/**
	 * Generate & Build & Tests all variants of JHipster 3.6.1. 
	 */
	@Test
	public void genJHipsterVariants() throws Exception{
		// False = Windows; True = Linux
		Boolean system = getValueOfSystem();

		initialization(system);

		//New array for file csv
		List<String[]> array = new ArrayList<String[]>();
		
		// 1 -> weightFolder -1 (UAA directory...)
		for (Integer i =1;i<=weightFolder-1;i++){

			String jDirectory = "jhipster"+i;

			//Strings used for the csv
			String generation = "?";
			String build = "?";
			String stacktraces = "?";
			String buildTime = "?";
			String buildMemory = "?";

			if (!system)
				//write .bat Scripts for windows
			{
				writeScriptBat("bashgitgenerate.bat","generate.sh",jDirectory);
				writeScriptBat("bashgitcompile.bat","compile.sh",jDirectory);
				writeScriptBat("bashgitbuild.bat","build.sh",jDirectory);
				writeScriptBat("bashgittest.bat","test.sh",jDirectory);
			}

			_log.info("Generate the App...");
			generateApp(jDirectory, system);
			_log.info("App Generated...");

			_log.info("Oracle generate "+i+" is done");

			_log.info("Check the generation of the App...");
			if(checkGenerateApp(jDirectory)){
				//String used for the generation csv
				generation ="OK";
				
				_log.info("Trying to compile the App...");
				compileApp(jDirectory, system);
			}
			else 
			{
				_log.error("App Generation Failure...");
				//String used for the generation csv
				generation ="KO";
			}

			boolean	checkGen = checkGenerateApp(jDirectory);
			if (checkGenerateApp(jDirectory))
			{
				_log.info("App Checked Success...-> build of the app");
				buildApp(jDirectory,system);
			}	
			else _log.info("App generation Failure...");

			_log.info("Oracle checkAndBuild "+i+" is done");

			_log.info("Check the build ...");
			boolean	checkBuild = checkBuildApp(jDirectory);
			if (checkGen & checkBuild)
			{
				//String build used for the csv
				build = "OK";
				stacktraces = "Nothing";
				buildTime = extractTimeBuild(jDirectory);
				buildMemory = extractMemoryBuild(jDirectory);
				_log.info("Build Success... Launch tests of the App...");
				testGenerateApp(jDirectory,system);
			}	
			else
			{
				//String build used for the csv
				build = "KO";
				_log.info("App Build Failure... Extract Stacktraces");
				stacktraces = extractStacktraces(jDirectory);
				buildTime = extractTimeBuild(jDirectory);
				buildMemory = extractMemoryBuild(jDirectory);
			}	

			_log.info("Oracle Tests "+i+" is done");

			_log.info("Writing into jhipster.csv");
			
			//New line for file csv
			String[] line = {jDirectory,generation,build,stacktraces,buildTime,buildMemory};
			//add line to Array
			array.add(line);

		}
		CSVUtils.writeToCSVList("jhipster.csv",array);
		termination();
	}
}