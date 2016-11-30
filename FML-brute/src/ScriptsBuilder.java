import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.xtext.util.Files;

/**
 * All Scripts related methods
 * 
 * @author Axel Halin
 * @author Alexandre Nuttinck
 */
public class ScriptsBuilder {

	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final String PROPERTIES_FILE = "System.properties";

	/**
	 * Generate all necessary script to generate/compile/run/test the application.
	 * 
	 * @param jconf Jhipster's Configuration
	 * @param jDirectory Directory of the configuration
	 */
	public void generateScripts(JhipsterConfiguration jconf, String jDirectory){
		generateYoJhipsterScript(jconf, jDirectory);
		generateCompileScript(jconf, jDirectory);		
		generateUnitTestScript(jconf, jDirectory);
		generateDockerScripts(jconf, jDirectory);			
		generateBuildScript(jconf, jDirectory);
		generateKillScript(jDirectory);
		generateOracleDatabaseScript(jconf, jDirectory);

		if (jconf.testFrameworks.length>0) {
			generateTestScript(jconf, jDirectory);
			generateTestDockerScript(jconf, jDirectory);
		}
		// TODO Alter if dev and prod profiles
		generateEntitiesScript(jDirectory,jconf.prodDatabaseType);
	}

	/**
	 * Generate the script to stop all databases services.
	 * 
	 * @param jDirectory Directory where the script must be generated.
	 */
	public void generateStopDatabaseScript(String jDirectory){
		Properties property = getProperties(PROPERTIES_FILE);
		String script = "#!/bin/bash\n\n"
				+ property.getProperty("mysqlStop")
				+ property.getProperty("cassandraStop")
				+ property.getProperty("mongodbStop")
				+ property.getProperty("postgreStop");

		Files.writeStringIntoFile(jDirectory+"/stopDB.sh", script);
	}

	public void generateStartDatabaseScript(String jDirectory){
		Properties property = getProperties(PROPERTIES_FILE);
		String script = "#!/bin/bash\n\n"
				+ property.getProperty("mysqlService")
				+ property.getProperty("cassandraService")
				+ property.getProperty("mongodbService")
				+ property.getProperty("postgreService");
		Files.writeStringIntoFile(jDirectory+"/startDB.sh", script);
	}


	/**
	 * Generates the script to generate the JHipster application.\n
	 * This script varies depending on the application type (see JHipster's Sub-Generators)
	 * 
	 * @param jconf JHipster Configuration to generate;
	 * @param jDirectory Name of the directory (jhipster+id) in which the script will be placed.
	 */
	private void generateYoJhipsterScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";

		if(jconf.applicationType.equals("clientApp")) script += "yo jhipster:client --auth session ";
		else if (jconf.applicationType.equals("serverApp")) script += "yo jhipster:server ";
		else script += "yo jhipster ";

		script += ">> generate.log 2>&1";

		Files.writeStringIntoFile(getjDirectory(jDirectory) + "generate.sh", script);
	}

	/**
	 * Generate the script to compile the JHipster application.
	 * 
	 * @param jconf Configuration of JHipster
	 * @param jDirectory Directory where the script must be generated
	 */
	private void generateCompileScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";
		if(jconf.buildTool.equals("maven")) script+= "mvn compile";
		else script+="./gradlew compileJava";

		script += ">> compile.log 2>&1";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"compile.sh", script);
	}

	/**
	 * Generate the script to build the application without the use of Docker.
	 * 
	 * @param jconf JHipster configuration to build.
	 * @param jDirectory Directory where the script must be generated.
	 */
	private void generateBuildScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";	

		switch (jconf.prodDatabaseType){
		case "mysql": 	script += getMysqlScript();
		break;
		case "mongodb": script += getMongodbScript();
		break;
		case "cassandra": 	script += getCassandraScript();
		break;
		case "postgresql": 	script += getPostgreScript();
		break;
		case "mariadb":	script += getMysqlScript();
		break;
		}
		
		// TODO See if we include dev profile for all variants
		if(jconf.devDatabaseType.startsWith("h2")){
			if(jconf.buildTool.equals("maven")) script += "./mvnw -Pdev ";
			else script += "./gradlew -Pdev ";
		} else{
			if(jconf.buildTool.equals("maven")) script += "./mvnw -Pprod ";
			else script += "./gradlew -Pprod ";
		}
		
		script += ">> build.log 2>&1";
		Files.writeStringIntoFile(getjDirectory(jDirectory) + "build.sh", script);
	}

	/**
	 * Generate script used to run tests on the configuration and write it in test.sh.
	 * 
	 * @param jconf Configuration on which tests are run.
	 * @param jDirectory Directory where to write the script.
	 */
	private void generateTestScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";

		for(String testFramework : jconf.testFrameworks){
			switch(testFramework){
			//TODO reactivate Gatling 
			/*case "gatling": if(jconf.buildTool.equals("maven"))
									script += "./mvnw gatling:execute";
								else
									script += "./gradlew gatlingRun -x cleanResources";
								script += " >> testGatling.log 2>&1\n";
								break;*/
			case "protractor": 	// TODO Server App must be running!
				script += "xvfb-run gulp protractor >> testProtractor.log 2>&1\n";
				break;
			case "cucumber": 	if (jconf.buildTool.equals("maven")) script += "./mvnw clean test >> cucumber.log 2>&1\n";
			else script += "./gradlew test >> cucumber.log 2>&1\n";
			break;
			}
		}
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"test.sh", script);
	}

	/**
	 * Generate script used to run tests on the configuration and write it in testDocker.sh.
	 * 
	 * @param jconf Configuration on which tests are run.
	 * @param jDirectory Directory where to write the script.
	 */
	private void generateTestDockerScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";

		for(String testFramework : jconf.testFrameworks){
			switch(testFramework){
			//TODO reactivate Gatling 
			/*case "gatling": if(jconf.buildTool.equals("maven"))
									script += "./mvnw gatling:execute";
								else
									script += "./gradlew gatlingRun -x cleanResources";
								script += " >> testDockerGatling.log 2>&1\n";
								break;*/
			case "protractor": 	script += "xvfb-run gulp protractor >> testDockerProtractor.log 2>&1\n";
			break;
			case "cucumber": 	if (jconf.buildTool.equals("maven")) script += "./mvnw clean test >> cucumberDocker.log 2>&1\n";
			else script += "./gradlew test >> cucumberDocker.log 2>&1\n";
			break; // Already tested in ./mvnw clean test
			}
		}
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"testDocker.sh", script);
	}

	/**
	 * Generate the script to run the unit test(s) of the application.
	 * 
	 * @param jconf JHipster configuration to test
	 * @param jDirectory Directory where the script must be generated
	 */
	private void generateUnitTestScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";
		if (jconf.buildTool.equals("maven")) script += "./mvnw clean test >> test.log 2>&1\n";
		else script += "./gradlew test >> test.log 2>&1\n";
		// KarmaJS is provided by default
		script += "gulp test >> testKarmaJS.log 2>&1\n";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"unitTest.sh", script);
	}


	/**
	 * Generates the scripts related to the use of Docker.
	 * 
	 * @param jconf
	 * @param jDirectory
	 */
	private void generateDockerScripts(JhipsterConfiguration jconf, String jDirectory){
		generateDockerStartScript(jDirectory, jconf);
		generateDockerStopScript(jconf, jDirectory);
	}					


	/**
	 * Generate the script used to package and deploy an application via Docker.
	 * 
	 * @param jDirectory Directory where the script must be generated.
	 * @param maven True if the application uses Maven; False otherwise.
	 */
	private void generateDockerStartScript(String jDirectory, JhipsterConfiguration jconf){
		Properties properties = getProperties(PROPERTIES_FILE);
		String script = "#!/bin/bash\n\n";
		if(jconf.databaseType.equals("cassandra")) script += properties.getProperty("cassandraMigration")+" >> cassandraMigration.log 2>&1";
		// Packaging
		if(jconf.buildTool.equals("maven")) script += properties.getProperty("mavenDockerPackage");
		else script += properties.getProperty("gradleDockerPackage");
		script+= " >> buildDocker.log 2>&1\n";
		// Docker-compose
		script += properties.getProperty("dockerStart")
				+ " >> buildDocker.log 2>&1\n";

		Files.writeStringIntoFile(getjDirectory(jDirectory)+"dockerStart.sh", script);
	}

	/**
	 * Generate the script used to stop the application via Docker and to remove all containers/images.
	 * 
	 * @param jconf JHipster configuration
	 * @param jDirectory Directory where the script must be generated.
	 */
	private void generateDockerStopScript(JhipsterConfiguration jconf, String jDirectory){
		Properties properties = getProperties(PROPERTIES_FILE);
		// Stop main container
		String script = "#!/bin/bash\n\n"
				+ properties.getProperty("dockerStop")
				+ " >> dockerStop.log 2>&1\n";
		// Stop database container
		switch (jconf.prodDatabaseType){
		case "mysql": 	script += properties.getProperty("dockerStopMysql");
		break;
		case "mongodb": script += properties.getProperty("dockerStopMongo");
		break;
		case "cassandra": 	script += properties.getProperty("dockerStopCassandra");
		break;
		case "postgresql": 	script += properties.getProperty("dockerStopPostgre");
		break;
		case "mariadb":		script += properties.getProperty("dockerStopMaria");
		break;
		}
		script += " >> dockerStop.log 2>&1\n";
		// Remove all Docker images
		script += properties.getProperty("dockerRemoveAll") + " >> dockerStop.log 2>&1\n";
		script += properties.getProperty("dockerRemoveImages") + " >> dockerStop.log 2>&1\n";

		Files.writeStringIntoFile(getjDirectory(jDirectory)+"dockerStop.sh", script);
	}

	/**
	 * Generates a script to kill the server running on port 8080 into killScript.sh.
	 * 
	 * @param jDirectory Directory where to write the script.
	 */
	private void generateKillScript(String jDirectory){
		String script = "#!/bin/bash\n\n";
		Properties properties = getProperties(PROPERTIES_FILE);

		script += properties.getProperty("killApp")+"\n";
		script += properties.getProperty("killRegistry")+"\n";
		script += properties.getProperty("killUAA")+"\n";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"killScript.sh", script);
	}

	private void generateEntitiesScript(String jDirectory, String database){
		String script = "#!/bin/bash\n\n";
		Properties properties = getProperties(PROPERTIES_FILE);

		if(database.equals("mysql") || database.equals("postgresql") || database.equals("mariadb"))
			script += properties.getProperty("importJDL")+"\n";
		else if(database.equals("mongodb"))
			script += properties.getProperty("importJDLMongo")+"\n";
		else script += properties.getProperty("importJDLCassandra")+"\n";
		script += properties.getProperty("generateJDL")+" >> generateJDL.log 2>&1\n";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"generateJDL.sh", script);
	}

	private void generateOracleDatabaseScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";
		Properties properties = getProperties(PROPERTIES_FILE);

		if(jconf.prodDatabaseType.equals("oracle")|jconf.devDatabaseType.equals("oracle"))
		{script += properties.getProperty("oracleInitJar")+"\n";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"oracleCopyJAR.sh", script);
		}
	}

	private static String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}


	/**
	 * Retrieve MySQL related scripts in the System.properties file.
	 * 
	 * @return MySQL script
	 */
	private String getMysqlScript(){
		Properties properties = getProperties(PROPERTIES_FILE);

		return 	properties.getProperty("mysqlService")
				+ properties.getProperty("mysqlInitiateConnexion")
				+ properties.getProperty("mysqlDropDatabase")
				+ properties.getProperty("mysqlCreateDatabase")
				+ properties.getProperty("mysqlCloseConnexion");
	}

	private String getCassandraScript(){
		Properties properties = getProperties(PROPERTIES_FILE);

		return 	properties.getProperty("cassandraService")
				+ properties.getProperty("cassandraDropKeySpace")
				+ properties.getProperty("cassandraCreateKeySpace")
				+ properties.getProperty("cassandraCreateTables")
				+ properties.getProperty("cassandraInsertUsers");
	}

	/**
	 * Get the script handling the initialization of a Mongodb database.
	 * 
	 * @return The script to initialize a MongoDB database.
	 */
	private String getMongodbScript(){
		Properties properties = getProperties(PROPERTIES_FILE);
		return properties.getProperty("mongodbService");
	}


	/**
	 * Get the script handling the initialization of a PostgreSQL database.
	 * 
	 * @return The script used to create the PostgreSQL database.
	 */
	private String getPostgreScript(){
		Properties properties = getProperties(PROPERTIES_FILE);

		return	properties.getProperty("postgreService")
				+ properties.getProperty("postgreInitiateConnexion")
				+ properties.getProperty("postgreDropDatabase")
				+ properties.getProperty("postgreCreateRole")
				+ properties.getProperty("postgreCreateDatabase")
				+ properties.getProperty("postgreCloseConnexion");
	}

	/**
	 * Retrieve all properties from specified property file.
	 * 
	 * @param propFileName Property file to retrieve
	 * @return All properties included in propFileName
	 */
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
}
