package oracle;

import csv.CSVUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	private static final String JS_COVERAGE_PATH = "target/test-results/coverage/report-lcov/lcov-report/index.html";
	private static final String JS_COVERAGE_PATH_GRADLE = "build/test-results/coverage/report-lcov/lcov-report/index.html";
	
	private static ResultChecker resultChecker = null;
	private static CSVUtils csvUtils = null;

	private static Thread threadRegistry;
	private static Thread threadUAA;

	private static void startProcess(String fileName, String desiredDirectory){
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

	/**
	 * Generate the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private static void generateApp(String jDirectory) throws InterruptedException, IOException{
		startProcess("./generate.sh",JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
	}

	/**
	 * Check the App is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private static boolean checkGenerateApp(String jDirectory) throws FileNotFoundException{

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
	private static void compileApp(String jDirectory){
		startProcess("./compile.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}

	/**
	 * Check the App is compile successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private static boolean checkCompileApp(String jDirectory) throws FileNotFoundException{
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
	private static void buildApp(String jDirectory) throws InterruptedException{
		startProcess("./build.sh", getjDirectory(jDirectory));
	}

	/**
	 * Launch UnitTests on the App is compiled successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 */
	private static void unitTestsApp(String jDirectory) throws InterruptedException{
		startProcess("./unitTest.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}


	/**
	 * Return the path to folder jDirectory (which is in the relative path JHIPSTERS_DIRECTORY/)
	 * 
	 * @param jDirectory Name of the folder
	 * @return The relative path to folder with name jDirectory.
	 */
	private static String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}

	/**
	 * Launch initialization scripts:\n
	 * 		- Start Uaa Server (in case of Uaa authentication)
	 * 		- Start Jhipster-Registry (in case of Microservices)
	 *  
	 * @param system Boolean to check OS (True = Linux, False = Windows)
	 */
	private static void initialization(boolean docker, String applicationType, String authentication){
		_log.info("Starting intialization scripts...");
		if(!docker){
			// Start database services
			startProcess("./startDB.sh","");
			if (applicationType.equals("\"gateway\"") || applicationType.equals("\"microservice\"") || applicationType.equals("\"uaa\"")){
				// Start Jhipster Registry
				threadRegistry = new Thread(new ThreadRegistry(projectDirectory+"/JHipster-Registry/"));
				threadRegistry.start();
				
				// Let Jhipster Registry initiate before attempting to launch UAA Server...
				try{Thread.sleep(30000);}
				catch(Exception e){_log.error(e.getMessage());}
				
				if(authentication.equals("\"uaa\"")){
					// Start UAA Server
					threadUAA = new Thread(new ThreadUAA(projectDirectory+"/"+JHIPSTERS_DIRECTORY+"/uaa/"));
					threadUAA.start();

					try{Thread.sleep(5000);}
					catch(Exception e){_log.error(e.getMessage());}
				}
			}
		} else{
			// STOP DB FOR DOCKER
			startProcess("./stopDB.sh","");
		}
		_log.info("Oracle intialized !");
	}

	/**
	 * Terminate the Oracle by ending JHipster Registry and UAA servers.
	 */
	private static void termination(){
		try{
			threadRegistry.interrupt();
			threadUAA.interrupt();
		} catch (Exception e){
			_log.error(e.getMessage());
		}
		
	}

	private static void cleanUp(String jDirectory, boolean docker){
		if (docker) startProcess("./dockerStop.sh", getjDirectory(jDirectory));
		else startProcess("./killScript.sh", getjDirectory(jDirectory));
	}

	private static void dockerCompose(String jDirectory){
		// Run the App
		startProcess("./dockerStart.sh",getjDirectory(jDirectory));
	}	

	
	/**
	 * Generate & Build & Tests all variants of JHipster 3.6.1. 
	 */
	/*@Test
	public void genJHipsterVariants() throws Exception{*/
	public static void main(String[] args) throws Exception{

		//Create CSV file JHipster if not exist.
		File f = new File("jhipster.csv");
		if(!f.exists()) { 
			_log.info("Create New CSV File JHipster");
			CSVUtils.createCSVFileJHipster("jhipster.csv"); 
		}
		//Create CSV file Coverage if not exist.
		File f2 = new File("coverageJACOCO.csv");
		if(!f2.exists()) { 
			_log.info("Create New CSV File Coverage");
			CSVUtils.createCSVFileCoverage("coverageJACOCO.csv"); 
		}
		
		CSVUtils.createCSVCucumber("cucumber.csv");

		// 1 -> weightFolder -1 (UAA directory...)
		for (Integer i =1;i<=weightFolder-1;i++){
			_log.info("Starting treatment of JHipster n° "+i);

			String jDirectory = "jhipster"+i;
			resultChecker = new ResultChecker(getjDirectory(jDirectory));
			
			//ID CSV ID used for jhipster,coverageJACOCO,cucumber csv
			String Id = "ND";
			// generate a new ID -> depend of the csv lenght
			Id = String.valueOf(f.length());

			//Strings used for the csv
			String generation = "X";
			String generationTime = "X";
			String stacktracesGen = "X";
			String compile = "KO";
			String compileTime = "ND";
			String stacktracesCompile = "ND";
			StringBuilder build = new StringBuilder("KO");
			String stacktracesBuild = "ND";
			String buildTime = "ND";
			StringBuilder buildWithDocker = new StringBuilder("KO");
			String stacktracesBuildWithDocker = "ND";
			String buildTimeWithDocker = "ND";
			String buildTimeWithDockerPackage = "ND";
			//jsonStrings
			String applicationType = "X";
			String authenticationType = "X";
			String hibernateCache = "X";
			String clusteredHttpSession = "X";
			String websocket = "X";
			String databaseType= "X";
			String devDatabaseType= "X";
			String prodDatabaseType= "X";
			String buildTool = "X";
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
			String gatlingDocker = "X";
			String protractorDocker = "X";
			StringBuilder imageSize = new StringBuilder("ND");
			String coverageInstuctions= "X";
			String coverageBranches= "X";
			String coverageJSStatements = "X";
			String coverageJSBranches = "X";

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
			if (object.get("buildTool") != null) buildTool = object.get("buildTool").toString();
			if (object.get("searchEngine") != null) searchEngine = object.get("searchEngine").toString();
			if (object.get("enableSocialSignIn") != null) enableSocialSignIn = object.get("enableSocialSignIn").toString();
			if (object.get("useSass") != null) useSass = object.get("useSass").toString();
			if (object.get("enableTranslation") != null) enableTranslation = object.get("enableTranslation").toString();
			if (object.get("testFrameworks") != null) testFrameworks = object.get("testFrameworks").toString();

			_log.info("Check if this config isn't done yet...");

			String[] yorc = {applicationType,authenticationType,hibernateCache,clusteredHttpSession,
					websocket,databaseType,devDatabaseType,prodDatabaseType,buildTool, searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks};
// TODO change buildTool in checknotexistlinecsv
			boolean check = CSVUtils.CheckNotExistLineCSV("jhipster.csv", yorc);

			// IF check TRUE the Generate else next
			
			if(check)
			{
				_log.info("Generating the App..."); 
				long millis = System.currentTimeMillis();
				generateApp(jDirectory);
				long millisAfterGenerate = System.currentTimeMillis();
				_log.info("Generation done!");

				_log.info("Checking the generation of the App...");

				if(checkGenerateApp(jDirectory)){
					generation ="OK";
					// Time to Generate
					Long generationTimeLong = millisAfterGenerate - millis;
					Double generationTimeDouble = generationTimeLong/1000.0;
					generationTime = generationTimeDouble.toString();
					stacktracesGen = resultChecker.extractStacktraces("generate.log");

					// TODO add .jhl file to create entities
					
					_log.info("Generation complete ! Trying to compile the App...");
					compileApp(jDirectory);

					if(checkCompileApp(jDirectory)){
						compile ="OK";
						compileTime = resultChecker.extractTime("compile.log");
						String[] partsCompile = compileTime.split(";");
						compileTime = partsCompile[0]; // delete the ";" used for Docker
						stacktracesCompile = resultChecker.extractStacktraces("compile.log");

						_log.info("Compilation success ! Launch Unit Tests...");
						unitTestsApp(jDirectory);
					
						resultsTest = resultChecker.extractResultsTest("test.log");
						karmaJS = resultChecker.extractKarmaJS("testKarmaJS.log");
						cucumber= resultChecker.extractCucumber("test.log");
						
						csvUtils = new CSVUtils(getjDirectory(jDirectory));
												
						// JACOCO Coverage results are only available with Maven
						if(buildTool.equals("\"maven\"")){
							coverageInstuctions= resultChecker.extractCoverageIntstructions("index.html");
							coverageBranches = resultChecker.extractCoverageBranches("index.html");
							coverageJSBranches = resultChecker.extractJSCoverageBranches(JS_COVERAGE_PATH);
							coverageJSStatements = resultChecker.extractJSCoverageStatements(JS_COVERAGE_PATH);
						} else{
							coverageJSBranches = resultChecker.extractJSCoverageBranches(JS_COVERAGE_PATH_GRADLE);
							coverageJSStatements = resultChecker.extractJSCoverageStatements(JS_COVERAGE_PATH_GRADLE);
						}
						
						//Extract CSV Coverage Data and write in coverage.csv
						csvUtils.writeLinesCoverageCSV("jacoco.csv","coverageJACOCO.csv",jDirectory,Id);

						_log.info("Compilation success ! Trying to build the App...");

						_log.info("Trying to build the App with Docker...");

						initialization(true, applicationType, authenticationType);
						imageSize = new StringBuilder();
						ThreadCheckBuild t1 = new ThreadCheckBuild(getjDirectory(jDirectory), true, "buildDocker.log",imageSize, buildWithDocker);
						t1.start();
						//build WITH docker
						dockerCompose(jDirectory);
						t1.done();
						
						if(imageSize.toString().equals("")){
							imageSize.delete(0, 5);
							imageSize.append("ND");
						}
						
						if(buildWithDocker.toString().equals("KO")) stacktracesBuildWithDocker = resultChecker.extractStacktraces("buildDocker.log");
						String buildTimeWithDockerVar = resultChecker.extractTime("buildDocker.log");
						String[] partsBuildWithDocker = buildTimeWithDockerVar.split(";");
						buildTimeWithDockerPackage = partsBuildWithDocker[0]; 
						if(partsBuildWithDocker.length>1) buildTimeWithDocker = partsBuildWithDocker[1]; 
						gatlingDocker = resultChecker.extractGatling("testDockerGatling.log");
						protractorDocker = resultChecker.extractProtractor("testDockerProtractor.log");
	
						_log.info("Cleaning up... Docker");
						cleanUp(jDirectory,true);

						// Building without Docker
						initialization(false, applicationType, authenticationType);
						ThreadCheckBuild t2 = new ThreadCheckBuild(getjDirectory(jDirectory), false, "build.log",imageSize,build);
						t2.start();
						_log.info("Trying to build the App without Docker...");
						//build WITHOUT docker
						buildApp(jDirectory);
						t2.done();
						cleanUp(jDirectory,false);
						
						if(build.toString().equals("KO")) stacktracesBuild = resultChecker.extractStacktraces("build.log");
						gatling = resultChecker.extractGatling("testGatling.log");
						protractor = resultChecker.extractProtractor("testProtractor.log");
						buildTime = resultChecker.extractTime("build.log");	
						String[] partsBuildWithoutDocker = buildTime.split(";");
						buildTime = partsBuildWithoutDocker[0]; // only two parts with Docker 
					} else{
						_log.error("App Compilation Failed ...");
						compile ="KO";
						compileTime = "KO";
						stacktracesBuild = "COMPILATION ERROR";
						stacktracesCompile = resultChecker.extractStacktraces("compile.log");
					}
				} else{
					_log.error("App Generation Failed...");
					generation ="KO";
					stacktracesBuild = "GENERATION ERROR";
					stacktracesGen = resultChecker.extractStacktraces("generate.log");
				}

				_log.info("Writing into jhipster.csv");

				//WITH DOCKER
				String docker = "true";

				//New line for file csv With Docker
				String[] line = {Id,jDirectory,docker,applicationType,authenticationType,hibernateCache,clusteredHttpSession,
						websocket,databaseType,devDatabaseType,prodDatabaseType,buildTool,searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks,
						generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,buildWithDocker.toString(),
						stacktracesBuildWithDocker,buildTimeWithDockerPackage,buildTimeWithDocker,imageSize.toString(),
						resultsTest,cucumber,karmaJS,gatlingDocker,protractorDocker,coverageInstuctions,coverageBranches,
						coverageJSStatements, coverageJSBranches};

				//write into CSV file
				CSVUtils.writeNewLineCSV("jhipster.csv",line);

				//WITHOUT DOCKER
				docker = "false";

				//New line for file csv without Docker
				String[] line2 = {Id,jDirectory,docker,applicationType,authenticationType,hibernateCache,clusteredHttpSession,
						websocket,databaseType,devDatabaseType,prodDatabaseType,buildTool,searchEngine,enableSocialSignIn,useSass,enableTranslation,testFrameworks,
						generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,build.toString(),stacktracesBuild,"NOTDOCKER",
						buildTime,"NOTDOCKER",resultsTest,cucumber,karmaJS,gatling,protractor,
						coverageInstuctions,coverageBranches, coverageJSStatements, coverageJSBranches};

				//write into CSV file
				CSVUtils.writeNewLineCSV("jhipster.csv",line2);
			}
			else {
				_log.info("This configuration has been already tested");
			}
		}
		_log.info("Termination...");
		termination();
	}
	
	/**
	 * Create CSV BUGS 
	 */
	@Test
	public void writeCSVBugs() throws Exception{
		//boolean false = not check doublon , true yes
		CSVUtils.createBugsCSV("jhipster.csv", "bugs.csv",false);
	}
}