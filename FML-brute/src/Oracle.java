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

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Oracle for all variants of configurator JHipster
 * 	- generate 
 * 	- build
 *  - tests
 * 
 * @author Nuttinck Alexandre
 *
 */
public class Oracle {

	private final static Logger _log = Logger.getLogger("JHipsterTest");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";

	private String projectDirectory = System.getProperty("user.dir");

	/**
	 * @param nameScriptBat Name of the Script .bat
	 * @param nameScriptSh Name of the Sript .sh
	 * @param jDirectory Name of the folder
	 */
	private void writeScriptBat(String nameScriptBat,String nameScriptSh,String jDirectory)
	{
		if (nameScriptBat != "bashgitlaunchDatabases.bat")
		{
			String buildScript = "cd "+getjDirectory(jDirectory)+"\n"; 

			buildScript += "\"C:/Program Files/Git/bin/sh.exe\" --login ./" + nameScriptSh;

			Files.writeStringIntoFile(getjDirectory(jDirectory) + nameScriptBat, buildScript);

		}
		else
		{
			String buildScript =  "\"C:/Program Files/Git/bin/sh.exe\" --login ./" + nameScriptSh;
			Files.writeStringIntoFile("bashgitlaunchDatabases.bat", buildScript);
		}

	}

	/**
	 * Creates the script to launch all databases 
	 * 
	 */
	private void writeScriptDatabases(){

		String buildScript = "#!/bin/bash\n\n";

		//CASSANDRA
		buildScript += "echo 'Cassandra!'\n";
		buildScript += "services cassandra start\n";
		buildScript += "cqlsh -f src/main/resources/config/cql/create-keyspace.cql\n";
		buildScript += "cqlsh -f src/main/resources/config/cql/changelog/00000000000000_create-tables.cql\n";
		buildScript += "cqlsh -f src/main/resources/config/cql/changelog/00000000000001_insert_default_users.cql\n";

		//MONGODB
		buildScript += "echo 'MongoDB!'\n";
		buildScript += "services mongodb start\n";

		// ORACLE TODO

		//POSTGRE

		buildScript += "echo 'Postgres!'\n";
		buildScript += "services pgservice start\n";
		buildScript += "psql -U postgres <<EOF\n";
		buildScript += "create role \"jhipster-fou\" login;\n";
		buildScript += "create database \"jhipster-fou\";\n";
		buildScript += "\\q\n";
		buildScript += "EOF\n";

		//MARIADB + MYSQL

		buildScript += "echo 'MariaDB-SQL!'\n";
		buildScript += "services mysql start\n";
		buildScript += "mysql -u root <<EOF\n";
		buildScript += "SET SESSION sql_mode = 'ANSI';\n";
		buildScript += "create database if not exists \"jhipster-fou\";\n";
		buildScript += "\\q\n";
		buildScript += "EOF\n";

		Files.writeStringIntoFile("launchDatabases.sh", buildScript);
	}

	/**
	 * Launch Databases.
	 * 
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void launchDatabases(boolean system) throws InterruptedException, IOException{

		// for windows add script bashgit.bat launch bashgit and execute generate.sh
		if (!system)
		{
			try {
				ProcessBuilder pb = new ProcessBuilder("bashgitlaunchDatabases.bat");
				pb.inheritIO();
				Process process = pb.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else
			//linux
		{
			try {
				ProcessBuilder pb2 = new ProcessBuilder("./launchDatabases.sh");
				//System.out.println("Current directory is: "+pb2.directory());
				pb2.inheritIO();
				Process process = pb2.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if (!system){
			try {
				ProcessBuilder pb = new ProcessBuilder(getjDirectory(jDirectory) + "bashgitgenerate.bat");
				pb.inheritIO();
				Process process = pb.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				ProcessBuilder pb2 = new ProcessBuilder("./generate.sh");
				pb2.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
				Process process = pb2.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

		while(m.find() | m2.find())

		{
			return true;
		} 

		return false;
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
		if (!system)

		{	
			try {
				ProcessBuilder pb = new ProcessBuilder(getjDirectory(jDirectory) + "bashgitbuild.bat");
				pb.inheritIO();
				Process process = pb.start();
				process.waitFor(200, TimeUnit.SECONDS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else

			//linux
		{
			try {
				ProcessBuilder pb2 = new ProcessBuilder("./build.sh");
				//System.out.println("Current directory is: "+pb2.directory());
				pb2.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
				pb2.inheritIO();
				Process process = pb2.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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


		//CHECK IF APPLICATION FAILED TO START THEN false
		Matcher m = Pattern.compile("((.*?)APPLICATION FAILED TO START)").matcher(text);

		while(m.find())

		{
			return false;

		} 

		return true;
	}

	/**
	 * Launch Tests on the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 */
	private void testGenerateApp(String jDirectory, boolean system) throws InterruptedException{

		// for windows add script bashgit.bat launch bashgit and execute test.sh
		if (!system)

		{
			try {
				ProcessBuilder pb = new ProcessBuilder(getjDirectory(jDirectory) + "bashgittest.bat");
				pb.inheritIO();
				Process process = pb.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else

			//linux
		{
			try {
				ProcessBuilder pb2 = new ProcessBuilder("./test.sh");
				//System.out.println("Current directory is: "+pb2.directory());
				pb2.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
				pb2.inheritIO();
				Process process = pb2.start();
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	private boolean getValueOfSystem() throws IOException {
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
			inputStream.close();
		}
		return true; // default linux
	}

	/**
	 * Generate & Build & Tests all variants of JHipster 3.6.1. 
	 */
	@Test
	public void genJHipsterVariants() throws Exception{

		//false = windows
		//true = linux
		Boolean system = getValueOfSystem();

		// weight of Folder Jhipsters
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;

		if (!system)
			//write .bat Scripts for windows
		{
			//launchDatabases
			writeScriptBat("bashgitlaunchDatabases.bat","launchDatabases.sh","");
		}

		// LAUNCH DATABASES AT THE BEGINING
		writeScriptDatabases();
		_log.info("Launch Databases...");
		launchDatabases(system);
		_log.info("Finish launch databases...");

		// 1 -> weightFolder 
		for (Integer i =1;i<=weightFolder;i++){

			String jDirectory = "jhipster"+i;

			if (!system)
				//write .bat Scripts for windows
			{
				writeScriptBat("bashgitgenerate.bat","generate.sh",jDirectory);
				writeScriptBat("bashgitbuild.bat","build.sh",jDirectory);
				writeScriptBat("bashgittest.bat","test.sh",jDirectory);
			}

			_log.info("Extracting files from jhipster "+i+"...");
			_log.info(getjDirectory(jDirectory));

			_log.info("Generate the App...");
			generateApp(jDirectory, system);
			_log.info("App Generated...");

			_log.info("Oracle generate "+i+" is done");

			_log.info("Check the generation of the App...");
			boolean	checkGen = checkGenerateApp(jDirectory);
			if (checkGen)
			{
				_log.info("App Checked Success...-> build of the app");
				buildApp(jDirectory,system);
			}	
			else
			{
				_log.info("App generation Failure...");
			}	

			_log.info("Oracle checkAndBuild "+i+" is done");

			_log.info("Check the build ...");
			boolean	checkBuild = checkBuildApp(jDirectory);
			if (checkGen & checkBuild)
			{
				_log.info("Build Success -> Launch tests of the App...");
				testGenerateApp(jDirectory,system);
			}	
			else
			{
				_log.info("App Build Failure...");
			}	

			_log.info("Oracle Tests "+i+" is done");

		}
	}
}
