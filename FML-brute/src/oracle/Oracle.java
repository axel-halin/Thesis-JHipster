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
	
	private static ResultChecker resultChecker = null;
	
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
	 * Launch initialization scripts:\n
	 * 		- Start Uaa Server (in case of Uaa authentication)
	 * 		- Start Jhipster-Registry (in case of Microservices)
	 *  
	 * @param system Boolean to check OS (True = Linux, False = Windows)
	 */
	private void initialization(){
		_log.info("Starting intialization scripts...");

		// If not Docker --> Launch everything manually
		//if(getProperties("System.properties").getProperty("useDocker").equals("false")){
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
		//} else{
			
			// STOP DB FOR DOCKER
			startProcess("./stopDB.sh","");
		//}
		_log.info("Oracle intialized !");
	}

	/**
	 * Terminate the Oracle by ending JHipster Registry and UAA servers.
	 */
	private void termination(){
		threadRegistry.interrupt();
		threadUAA.interrupt();
	}

	private void cleanUp(String jDirectory){
		startProcess("./dockerStop.sh", getjDirectory(jDirectory));
	}

	private void dockerCompose(String jDirectory){
		// Run the App
		startProcess("./dockerStart.sh",jDirectory, 250, TimeUnit.SECONDS);
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
	public void testJHipsterVariants() throws Exception{
		
		resultChecker = new ResultChecker(getjDirectory("jhipster4"));
		System.out.println(resultChecker.extractStacktraces("build.log"));
		
	}
	
	/**
	 * Generate & Build & Tests all variants of JHipster 3.6.1. 
	 */
	@Test
	public void genJHipsterVariants() throws Exception{

		initialization();

		//Create CSV file.
		CSVUtils.createCSVFile("jhipster.csv");

		// 1 -> weightFolder -1 (UAA directory...)
		for (Integer i =1;i<=2-1;i++){
			_log.info("Starting treatment of JHipster n° "+i);
			
			String jDirectory = "jhipster"+i;
			resultChecker = new ResultChecker(getjDirectory(jDirectory));
			
			//boolean docker needed to build with and without docker.  
			//boolean docker = false;

			//Strings used for the csv
			String generation = "?";
			String generationTime = "?";
			String stacktracesGen = "?";
			String compile = "?";
			String compileTime = "?";
			String stacktracesCompile = "?";
			String build = "?";
			String stacktracesBuild = "?";
			String buildTime = "?";
			String buildMemory = "?";
			String buildWithDocker = "?";
			String stacktracesBuildWithDocker = "?";
			String buildTimeWithDocker = "?";
			String buildMemoryWithDocker = "?";
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
			long millis = System.currentTimeMillis();
			generateApp(jDirectory);
			long millisAfterGenerate = System.currentTimeMillis();
			_log.info("Generation done!");


			_log.info("Checking the generation of the App...");
			
			if(checkGenerateApp(jDirectory)){
				generation ="OK";
				// Time to Generate
				
				// TODO NEED VERIFICATION
				Long generationTimeLong = millisAfterGenerate - millis;
				Double generationTimeDouble = generationTimeLong/1000.0;
				generationTime = generationTimeDouble.toString();
				stacktracesGen = resultChecker.extractStacktraces("generate.log");

				_log.info("Generation complete ! Trying to compile the App...");
				compileApp(jDirectory);

				if(checkCompileApp(jDirectory)){
					compile ="OK";
					compileTime = resultChecker.extractTime("compile.log");
					stacktracesCompile = resultChecker.extractStacktraces("compile.log");

					_log.info("Compilation success ! Trying to build the App...");
					//Properties properties = getProperties("System.properties");
					//if(properties.getProperty("useDocker").equals("true"))
					//dockerCompose(jDirectory);
					//else buildApp(jDirectory);
					
					_log.info("Trying to build the App with Docker...");
					//build WITH docker
					dockerCompose(jDirectory);
					
					if(resultChecker.checkBuildApp("buildDocker.log"))
					{
						//String build used for the csv
						buildWithDocker = "OK";
						stacktracesBuildWithDocker = resultChecker.extractStacktraces("buildDocker.log");
						buildTimeWithDocker = resultChecker.extractTime("buildDocker.log");
						buildMemoryWithDocker = resultChecker.extractMemoryBuild("buildDocker.log");
						// TODO Redeploy the app
						/*						Thread thread = new Thread(new ThreadDeploy(system,"./dockerStart.sh",projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
												thread.start();

												Thread.sleep(50000);
												testsApp(jDirectory,system);
												thread.interrupt();
												// Stop the App
												startProcess("./dockerStop.sh",system,JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");*/
						_log.info("Build Success... Launch tests of the App...");

					} else{
						//String build used for the csv
						build = "KO";
						_log.info("App Build Failure... Extract Stacktraces");
						stacktracesBuild = resultChecker.extractStacktraces("buildDocker.log");
						buildTime = "KO";
						buildMemory = "KO";
					}	
					
					_log.info("Cleaning up... Docker");
					cleanUp(jDirectory);
					
					_log.info("Trying to build the App without Docker...");
					//build WITHOUT docker
					buildApp(jDirectory);
					
					if(resultChecker.checkBuildApp("build.log"))
					{
						//String build used for the csv
						build = "OK";
						stacktracesBuild = resultChecker.extractStacktraces("build.log");
						buildTime = resultChecker.extractTime("build.log");
						buildMemory = resultChecker.extractMemoryBuild("build.log");

						_log.info("Build Success... Launch tests of the App...");
					} else{
						//String build used for the csv
						build = "KO";
						_log.info("App Build Failure... Extract Stacktraces");
						stacktracesBuild = resultChecker.extractStacktraces("build.log");
						buildTime = "KO";
						buildMemory = "KO";
					}	
					
				} else{
					_log.error("App Compilation Failed ...");
					compile ="KO";
					stacktracesCompile = resultChecker.extractStacktraces("compile.log");
				}
			} else{
				_log.error("App Generation Failed...");
				generation ="KO";
				stacktracesGen = resultChecker.extractStacktraces("generate.log");
			}
			
			//extract From Tests TODO: Handle file not found in methods
			try{
				resultsTest= resultChecker.extractResultsTest("test.log");
				cucumber= resultChecker.extractCucumber("testProtractor.log");
				karmaJS= resultChecker.extractKarmaJS("testKarmaJS.log");
				gatling = resultChecker.extractGatling("testGatling.log");
				protractor = resultChecker.extractProtractor("testProtractor.log");
			} catch(Exception e){
				_log.error(e.getMessage());
			}
			
			_log.info("Writing into jhipster.csv");
			
			//WITH DOCKER
			String docker = "true";
			
			//New line for file csv With Docker
			String[] line = {jDirectory,docker,applicationType,authenticationType,hibernateCache,clusteredHttpSession,
					websocket,databaseType,devDatabaseType,prodDatabaseType,searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks,
					generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,buildWithDocker,stacktracesBuildWithDocker,buildTimeWithDocker,buildMemoryWithDocker,
					resultsTest,cucumber,karmaJS,gatling,protractor};
			
			//write into CSV file
			CSVUtils.writeNewLineCSV("jhipster.csv",line);
			
			//WITHOUT DOCKER
			 docker = "false";
			
			//New line for file csv without Docker
			String[] line2 = {jDirectory,docker,applicationType,authenticationType,hibernateCache,clusteredHttpSession,
					websocket,databaseType,devDatabaseType,prodDatabaseType,searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks,
					generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,build,stacktracesBuild,buildTime,buildMemory,
					resultsTest,cucumber,karmaJS,gatling,protractor};

			//write into CSV file
			CSVUtils.writeNewLineCSV("jhipster.csv",line2);
		}
		
		_log.info("Termination...");
		termination();
		
	}
}