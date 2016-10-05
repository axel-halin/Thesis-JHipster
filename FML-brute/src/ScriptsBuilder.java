import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.xtext.util.Files;

/**
 * All Scripts related methods
 * 
 * @author Axel
 */
public class ScriptsBuilder {

	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final String PROPERTIES_FILE = "System.properties";
	
	public void generateYoJhipsterScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";
		
		if(jconf.applicationType.equals("clientApp")) script += "yo jhipster:client --auth session ";
		else if (jconf.applicationType.equals("serverApp")) script += "yo jhipster:server ";
		else script += "yo jhipster ";
		
		script += ">> generate.log 2>&1";
		
		Files.writeStringIntoFile(getjDirectory(jDirectory) + "generate.sh", script);
	}
	
	
	public void generateCompileScript(JhipsterConfiguration jconf, String jDirectory){
		String script = "#!/bin/bash\n\n";
		if(jconf.buildTool.equals("maven")) script+= "mvn compile";
		else script+="./gradlew compileJava";
		
		script += ">> compile.log 2>&1";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"compile.sh", script);
	}
	
	
	public void generateBuildScript(JhipsterConfiguration jconf, String jDirectory){
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
		
		if(jconf.buildTool.equals("maven")) script += "./mvnw -Pprod ";
		else script += "./gradlew -Pprod ";
		
		script += ">> build.log 2>&1";
		Files.writeStringIntoFile(getjDirectory(jDirectory) + "build.sh", script);
	}
	
	//TODO
	public void generateTestScript(JhipsterConfiguration jconf, String jDirectory){}
	
	
	public void generateKillScript(String jDirectory){
		String script = "#!/bin/bash\n\n";
		
		script += "fuser -k  8080/tcp";
		Files.writeStringIntoFile(getjDirectory(jDirectory)+"killServer.sh", script);
	}
	
	public static String getjDirectory(String jDirectory) {
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
				+ properties.getProperty("cassandraCreateKeySpace")
				+ properties.getProperty("cassandraCreateTables")
				+ properties.getProperty("cassandraInsertUsers");
	}
	
	
	private String getMongodbScript(){
		Properties properties = getProperties(PROPERTIES_FILE);
		return properties.getProperty("mongodbService");
	}
	
	private String getPostgreScript(){
		Properties properties = getProperties(PROPERTIES_FILE);
		
		return	properties.getProperty("postgreService")
				+ properties.getProperty("postgreInitiateConnexion")
				+ properties.getProperty("postgreDropDatabase")
				+ properties.getProperty("postgreCreateRole")
				+ properties.getProperty("postgreCreateDatabase")
				+ properties.getProperty("postgreCloseConnexion");
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
}
