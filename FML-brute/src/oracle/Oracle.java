package oracle;

import csv.CSVUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	private static final String projectDirectory = System.getProperty("user.dir");
	
	private Thread threadRegistry;
	private Thread threadUAA;

	private void startProcess(String fileName, String desiredDirectory){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			processBuilder.directory(new File(projectDirectory + "/" + desiredDirectory));
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

	private void startProcess(String fileName, String jDirectory, long timeOut, TimeUnit unit){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			processBuilder.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
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
	private void generateApp(String jDirectory) throws InterruptedException, IOException{
		startProcess("./generate.sh",JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
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
	private void compileApp(String jDirectory){
		startProcess("./compile.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}

	/**
	 * Check the App is compile successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkCompileApp(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "compile.log");

		//CHECK IF BUILD FAILED THEN false
		Matcher m1 = Pattern.compile("((.*?)BUILD FAILED)").matcher(text);
		Matcher m2 = Pattern.compile("((.*?)BUILD FAILURE)").matcher(text);

		while(m1.find() | m2.find()) return false;
		return true;
	}

	/**
	 * Build the App which is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 */
	private void buildApp(String jDirectory) throws InterruptedException{
		startProcess("./build.sh", jDirectory, 150, TimeUnit.SECONDS);
		// Kill server after timeout
		startProcess("./killScript.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
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
	private void testsApp(String jDirectory, boolean system) throws InterruptedException{
		startProcess("./test.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
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
		Properties properties = getProperties("System.properties");
		return Boolean.parseBoolean(properties.getProperty("system"));
	}

	/**
	 * Return stacktraces
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of stacktraces
	 * 
	 */
	private String extractStacktraces(String jDirectory, String log) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + log);

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

		Matcher m1 = Pattern.compile("Started JhipsterApp in (.*?) seconds").matcher(text);

		String timebuild = "NOTFIND";

		while(m1.find())
		{
			System.out.println(m1.toString());
			return timebuild = m1.group(1).toString();
		}

		return timebuild;
	}

	/**
	 * Return the memory used   
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractMemoryBuild(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "build.log");
		
		Matcher m1 = Pattern.compile("(.*?)Final Memory").matcher(text);

		String memoryBuild = "NOTFIND";

		while(m1.find())
		{
			return memoryBuild = m1.toString();
		}

		return memoryBuild;
	}

	/**
	 * Return results from tests ./mvnw clean test | ./gradlew clean test  
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractResultsTest(String jDirectory) throws FileNotFoundException{

		String text = "";
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "test.log");
		
		Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.repository.CustomSocialUsersConnectionRepositoryIntTest").matcher(text);
		Matcher m2 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.security.SecurityUtilsUnitTest").matcher(text);
		Matcher m3 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.SocialServiceIntTest").matcher(text);
		Matcher m4 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.UserServiceIntTest").matcher(text);
		Matcher m5 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AccountResourceIntTest").matcher(text);
		Matcher m6 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AuditResourceIntTest").matcher(text);
		Matcher m7 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.UserResourceIntTest").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find()|m2.find()|m3.find()|m4.find()|m5.find()|m6.find()|m7.find())
		{
			return resultsTests = m1.group().toString() +"\n"+ m2.group().toString() +"\n"+
					m3.group().toString() +"\n"+ m4.group().toString() +"\n"+ m5.group().toString() +"\n"+
					m6.group().toString() +"\n"+ m7.group().toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from tests ./mvnw clean test | ./gradlew clean test -> cucumber
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractCucumber(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "test.log");

		Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.cucumber.CucumberTest").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find())
		{
			return resultsTests = m1.group().toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from : gulp test 
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	private String extractKarmaJS(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "testKarmaJS.log");

		Matcher m1 = Pattern.compile("(.*?) FAILED").matcher(text);

		String resultsTests = "SUCCESS";

		while(m1.find())
		{
			return resultsTests = m1.group().toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from tests ./mvnw gatling:execute	| ./gradlew gatlingRun -x cleanResources
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 * TODO Create sequence of tests for gatling.
	 */
	private String extractGatling(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "testGatling.log");

		Matcher m1 = Pattern.compile("(.*?)").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find())
		{
			return resultsTests = m1.group(1).toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from tests gulp protractor
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 * TODO Create sequence of tests for gatling.
	 */
	private String extractProtractor(String jDirectory) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "testProtractor.log");

		Matcher m1 = Pattern.compile("(.*?) specs, (.*?) failures").matcher(text);
		
		String resultsTests = "NOTFIND";

		while(m1.find())
			{
			return resultsTests = m1.group().toString();
			}
		
		return resultsTests;
	}


	/**
	 * Launch initialization scripts:\n
	 * 		- Start Uaa Server (in case of Uaa authentication)
	 * 		- Start Jhipster-Registry (in case of Microservices)
	 *  
	 * @param system Boolean to check OS (True = Linux, False = Windows)
	 */
	private void initialization(){
		_log.info("Starting intialization scripts...");

		// Start Jhipster Registry
		threadRegistry = new Thread(new ThreadRegistry(projectDirectory+"/JHipster-Registry/"));
		threadRegistry.start();

		// Let Jhipster Registry initiate before attempting to launch UAA Server...
		try{Thread.sleep(30000);}
		catch(Exception e){_log.error(e.getMessage());}

		// Start UAA Server
		threadUAA = new Thread(new ThreadUAA(projectDirectory+"/"+JHIPSTERS_DIRECTORY+"/uaa/"));
		threadUAA.start();

		try{Thread.sleep(5000);}
		catch(Exception e){_log.error(e.getMessage());}

		_log.info("Oracle intialized !");
	}

	/**
	 * Terminate the Oracle by ending JHipster Registry and UAA servers.
	 */
	private void termination(){
		threadRegistry.interrupt();
		threadUAA.interrupt();
	}

	// TODO Add Windows Support ?
	private void dockerCompose(String jDirectory){
		// Package the App
		startProcess("./dockerPackage.sh",JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
		// Run the App
		startProcess("./dockerStart.sh",jDirectory, 150, TimeUnit.SECONDS);
		// Stop the App
		startProcess("./dockerStop.sh",JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
	}
	
	private Properties getProperties(String propFileName) {
		InputStream inputStream = null;
		Properties prop = new Properties();
		try {
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) prop.load(inputStream);
			else throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try{inputStream.close();}
			catch (IOException e){e.printStackTrace();}
		}
		return prop; // default linux
	}
	
	/**
	 * Generate & Build & Tests all variants of JHipster 3.6.1. 
	 */
	@Test
	public void genJHipsterVariants() throws Exception{

		// Init only if not Docker
		if(getProperties("System.properties").getProperty("useDocker").equals("false"))
			initialization();

		//Create CSV file.
		CSVUtils.createCSVFile("jhipster.csv");

		// 1 -> weightFolder -1 (UAA directory...)
		for (Integer i =1;i<=weightFolder-1;i++){

			_log.info("Starting treatment of JHipster n° "+i);
			
			String jDirectory = "jhipster"+i;

			//Strings used for the csv
			String generation = "?";
			String stacktracesGen = "?";
			String compile = "?";
			String stacktracesCompile = "?";
			String build = "?";
			String stacktracesBuild = "?";
			String buildTime = "?";
			String buildMemory = "?";
			//jsonStrings
			String applicationType = "X";
			String authenticationType = "X";
			String hibernateCache = "X";
			String clusteredHttpSession = "X";
			String websocket = "X";
			String databaseType= "X";
			String devDatabaseType= "X";
			String prodDatabaseType= "X";
			String searchEngine= "X";
			String enableSocialSignIn= "X";
			String useSass= "X";
			String enableTranslation = "X";
			String testFrameworks ="X";
			//Tests part
			String resultsTest= "X";
			String cucumber= "X";
			String karmaJS= "X";
			String gatling = "X";
			String protractor = "X";

			//Get Json strings used for the csv
			JsonParser jsonParser = new JsonParser();
			JsonObject objectGen = jsonParser.parse(Files.readFileIntoString(getjDirectory(jDirectory)+".yo-rc.json")).getAsJsonObject();
			JsonObject object = (JsonObject) objectGen.get("generator-jhipster");

			if (object.get("applicationType") != null) applicationType = object.get("applicationType").toString();
			if (object.get("authenticationType") != null) authenticationType = object.get("authenticationType").toString();
			if (object.get("hibernateCache") != null) hibernateCache = object.get("hibernateCache").toString();
			if (object.get("clusteredHttpSession") != null) clusteredHttpSession = object.get("clusteredHttpSession").toString();
			if (object.get("websocket") != null) websocket = object.get("websocket").toString();
			if (object.get("databaseType") != null) databaseType = object.get("databaseType").toString();
			if (object.get("devDatabaseType") != null) devDatabaseType = object.get("devDatabaseType").toString();
			if (object.get("prodDatabaseType") != null) prodDatabaseType = object.get("prodDatabaseType").toString();
			if (object.get("searchEngine") != null) searchEngine = object.get("buildTool").toString();
			if (object.get("enableSocialSignIn") != null) enableSocialSignIn = object.get("enableSocialSignIn").toString();
			if (object.get("useSass") != null) useSass = object.get("useSass").toString();
			if (object.get("enableTranslation") != null) enableTranslation = object.get("enableTranslation").toString();
			if (object.get("testFrameworks") != null) testFrameworks = object.get("testFrameworks").toString();

			_log.info("Generating the App...");
			generateApp(jDirectory);
			_log.info("Generation done!");


			_log.info("Checking the generation of the App...");
			
			if(checkGenerateApp(jDirectory)){
				generation ="OK";
				stacktracesGen = extractStacktraces(jDirectory,"generate.log");

				_log.info("Generation complete ! Trying to compile the App...");
				compileApp(jDirectory);

				if(checkCompileApp(jDirectory)){
					compile ="OK";
					stacktracesCompile = extractStacktraces(jDirectory,"compile.log");

					_log.info("Compilation success ! Trying to build the App...");
					Properties properties = getProperties("System.properties");
					if(properties.getProperty("useDocker").equals("true"))
						dockerCompose(jDirectory);
					else buildApp(jDirectory);
					
					if(checkBuildApp(jDirectory))
					{
						//String build used for the csv
						build = "OK";
						stacktracesBuild = extractStacktraces(jDirectory,"build.log");
						buildTime = extractTimeBuild(jDirectory);
						buildMemory = extractMemoryBuild(jDirectory);

						_log.info("Build Success... Launch tests of the App...");
						// TODO Redeploy the app
/*						Thread thread = new Thread(new ThreadDeploy(system,"./dockerStart.sh",projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
						thread.start();

						Thread.sleep(50000);
						testsApp(jDirectory,system);
						thread.interrupt();
						// Stop the App
						startProcess("./dockerStop.sh",system,JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");*/
					} else{
						//String build used for the csv
						build = "KO";
						_log.info("App Build Failure... Extract Stacktraces");
						stacktracesBuild = extractStacktraces(jDirectory,"build.log");
						buildTime = "KO";
						buildMemory = "KO";
					}	
				} else{
					_log.error("App Compilation Failed ...");
					compile ="KO";
					stacktracesCompile = extractStacktraces(jDirectory,"compile.log");
				}
			} else{
				_log.error("App Generation Failed...");
				generation ="KO";
				stacktracesGen = extractStacktraces(jDirectory,"generate.log");
			}
			
			//extract From Tests
			/*resultsTest= extractResultsTest(jDirectory);
			cucumber= extractCucumber(jDirectory);
			karmaJS= extractKarmaJS(jDirectory);
			gatling = extractGatling(jDirectory);
			protractor = extractProtractor(jDirectory);*/

			_log.info("Writing into jhipster.csv");

			//New line for file csv
			String[] line = {jDirectory,applicationType,authenticationType,hibernateCache,clusteredHttpSession,
					websocket,databaseType,devDatabaseType,prodDatabaseType,searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks,
					generation,stacktracesGen,compile,stacktracesCompile,build,stacktracesBuild,buildTime,buildMemory,
					resultsTest,cucumber,karmaJS,gatling,protractor};

			//write into CSV file
			CSVUtils.writeNewLineCSV("jhipster.csv",line);
		}
		
		if(getProperties("System.properties").getProperty("useDocker").equals("false"))
			termination();
	}
}