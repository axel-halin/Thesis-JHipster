import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;
import org.prop4j.Node;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.familiar.FMLTest; 
import fr.familiar.operations.featureide.SATFMLFormula;
import fr.familiar.variable.FeatureModelVariable;
import fr.familiar.variable.FeatureVariable;
import fr.familiar.variable.SetVariable;
import fr.familiar.variable.Variable;

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Generator for all variants of configurator JHipster
 * 
 * @author Mathieu ACHER
 * @author Axel HALIN
 *
 */
public class JHipsterTest extends FMLTest{

	private final static Logger _log = Logger.getLogger("JHipsterTest");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final String DIMACS_FILENAME = "models/model.dimacs";
	
	/**
	 * Modeling of JHipster's Feature Model to FeatureIDE notation.
	 * 
	 * @return The Feature Model of JHipster in a FeatureModelVariable.
	 * @throws Exception If the syntax of the modeling is wrong.
	 */
	private FeatureModelVariable getFMJHipster() throws Exception {
		return FM ("jhipster", "FM ( jhipster :  Base ; Base : "
				+ "[InternationalizationSupport] [Database] [Authentication] Generator [Libsass] [SpringWebSockets] [SocialLogin] [ClusteredSession] [TestingFrameworks] [BackEnd] ; "
				+ "Authentication : (HTTPSession | Uaa | OAuth2 | JWT) ; "
				+ "TestingFrameworks : [Gatling] [Protractor] [Cucumber] ; "
				+ "Generator : (Server | Application | Client) ; "
				+ "Server : (MicroserviceApplication | UaaServer | ServerApp) ; "
				+ "Application : (MicroserviceGateway | Monolithic) ; "
				+ "Database : (SQL | Cassandra | MongoDB) ; "
				+ "SQL: Development [Hibernate2ndLvlCache] Production [ElasticSearch] ; "
				+ "Development : (Oracle12c | H2 | PostgreSQLDev | MariaDBDev | MySql) ; "
				+ "H2 : (DiskBased | InMemory) ; "
				+ "Hibernate2ndLvlCache : (HazelCast | EhCache) ; " 
				+ "Production : (MySQL | Oracle | MariaDB | PostgreSQL) ; "
				+ "BackEnd : (Gradle | Maven) ; "
				// Constraints
				+ "(OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB) ; "
				+ "SocialLogin -> ((HTTPSession | JWT) & (ServerApp | Monolithic) & (SQL | MongoDB)) ; "
				+ "UaaServer -> Uaa ; "
				+ "Oracle -> (H2 | Oracle12c) ; "
				+ "(!OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB | Cassandra) ; "
				+ "Server -> !Protractor ; "
				+ "MySQL -> (H2 | MySql) ; "
				+ "(MicroserviceApplication | MicroserviceGateway) -> (JWT | Uaa) ; "
				+ "Monolithic -> (JWT | HTTPSession | OAuth2) ; "
				+ "MariaDB -> (H2 | MariaDBDev) ; "
				+ "PostgreSQL -> (H2 | PostgreSQLDev) ; "
				+ "(Server | Application) -> (BackEnd & Authentication) ; "
				+ "(SpringWebSockets | ClusteredSession) -> Application ; "
				+ "Client -> (!Gatling & !Cucumber & !BackEnd & !Authentication) ; "
				+ "Libsass -> (Application | Client) ; "
				+ " )");
	}
	
	
	/**
	 * Instantiate a JhipsterConfiguration object which attributes values are present in the Set of Strings.
	 * i.e a new JhipsterConfiguration based on a specific configuration of JHipster.
	 * 
	 * In this version, we do not handle jhipster:client nor jhipster:server.
	 * 
	 * @param strConfs Set of all features representing a specific configuration of JHipster
	 * @param fmvJhipster Feature Model of JHipster.
	 * @return JhipsterConfiguration object representing a specific configuration (strConfs)
	 */
	private JhipsterConfiguration toJhipsterConfiguration(Set<String> strConfs, FeatureModelVariable fmvJhipster) {
		// Foo values
		final String BASENAME = "jhipster";
		final String PACKAGENAME = "io.variability.jhipster";
		final String SERVERPORT = "8080";
		final String JHIPSTERVERSION = "3.6.1";
		final String JHIPREFIX = "jhi";
		final String SESSIONKEY = "13e6029734fb9984533b9bb5c511bca6d624c6ed";
		final String JWTKEY = "d8837e4a671d25456432b55b1e4a99fe0356ed07";
		final String UAABASENAME = "uaa";
		
		JhipsterConfiguration jhipsterConf = new JhipsterConfiguration();
		
		// Find application type
		switch (get("Generator", strConfs, fmvJhipster)){
			case "Server": 	jhipsterConf.applicationType = get("Server", strConfs, fmvJhipster);
							break;
			case "Application": jhipsterConf.applicationType = get("Application", strConfs, fmvJhipster);
								break;
			case "Client": 	jhipsterConf.applicationType="clientApp";
							break;
		}
		
		if(!jhipsterConf.applicationType.equals("clientApp")){
			// Common attributes
			jhipsterConf.jhipsterVersion = JHIPSTERVERSION;
			jhipsterConf.baseName = BASENAME;
			jhipsterConf.packageName = PACKAGENAME;
			jhipsterConf.packageFolder = PACKAGENAME.replace(".", "/");
			jhipsterConf.serverPort = SERVERPORT;
			jhipsterConf.authenticationType = get("Authentication", strConfs, fmvJhipster);
			jhipsterConf.hibernateCache = falseByNo(get("Hibernate2ndLvlCache", strConfs, fmvJhipster));
			// Common to ServerApp and Monolith application
			if(jhipsterConf.applicationType.equals("serverApp") || jhipsterConf.applicationType.equals("monolith") || jhipsterConf.applicationType.equals("gateway")){
				jhipsterConf.clusteredHttpSession = falseByNo(get("ClusteredSession", strConfs, fmvJhipster));
				jhipsterConf.websocket = falseByNo(get("SpringWebSockets", strConfs, fmvJhipster));
				jhipsterConf.enableSocialSignIn = Boolean.parseBoolean(isIncluded("SocialLogin", strConfs));
			}
			// Common attributes
			jhipsterConf.databaseType = falseByNo(get("Database", strConfs, fmvJhipster));
			jhipsterConf.devDatabaseType = falseByNo(get("Development", strConfs, fmvJhipster));
			if (jhipsterConf.devDatabaseType.equals("H2")){jhipsterConf.devDatabaseType=get("H2", strConfs, fmvJhipster);}
			jhipsterConf.prodDatabaseType = falseByNo(get("Production", strConfs, fmvJhipster));
			jhipsterConf.searchEngine = falseByNo(get("ElasticSearch", strConfs, fmvJhipster));
			jhipsterConf.buildTool = get("BackEnd", strConfs, fmvJhipster);
			// Authentication Key
			if (jhipsterConf.authenticationType.equals("jwt")){jhipsterConf.jwtSecretKey = JWTKEY;}
			else if (jhipsterConf.authenticationType.equals("session")){jhipsterConf.rememberMeKey = SESSIONKEY;}
			else if (jhipsterConf.authenticationType.equals("uaa")){jhipsterConf.uaaBaseName = UAABASENAME;}
			// Not for ServerApp
			if (!jhipsterConf.applicationType.equals("serverApp")){
				jhipsterConf.jhiPrefix = JHIPREFIX;
				jhipsterConf.testFrameworks = gets("TestingFrameworks", strConfs, fmvJhipster);
			}
		}
		
		
		if (jhipsterConf.applicationType.equals("microservice") || jhipsterConf.applicationType.equals("uaa")) 
			jhipsterConf.skipClient = true;
		
		if (jhipsterConf.applicationType.equals("microservice") || 
				(jhipsterConf.applicationType.equals("gateway") && jhipsterConf.authenticationType.equals("uaa"))){
			jhipsterConf.skipUserManagement = true;
		}
		
		if (jhipsterConf.applicationType.equals("monolith") || jhipsterConf.applicationType.equals("clientApp") || jhipsterConf.applicationType.equals("gateway")) 
			jhipsterConf.useSass = Boolean.parseBoolean(isIncluded("LibSass", strConfs));
		
		
		// If internationalization support (we limit ourselves to english for now)
		// TODO support languages
		/*if (jhipsterConf.enableTranslation){
			jhipsterConf.nativeLanguage = "en";
			//jhipsterConf.languages = "en";
		}*/
		jhipsterConf.enableTranslation = false;
		
		return jhipsterConf;
	}
	
	
	/**
	 * Creates the script to generate JHipster application based on the yo-rc.json and to run it.
	 * 
	 * @param conf JHipster configuration to be generated.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void writeScript(JhipsterConfiguration conf, String jDirectory){
		String buildScript = "#!/bin/bash\n\n"
							+ "ofolder=`pwd`\n" 
							+ "yo jhipster >> jhipsteryo.log 2>&1\n";
		// Need to create database (keyspace) and schema + insert data.
		if(conf.databaseType.equals("cassandra")){
			buildScript += "echo 'Cassandra!'\n";
			buildScript += "sudo services cassandra start\n";
			buildScript += "sudo cqlsh -f src/main/resources/config/cql/create-keyspace.cql\n";
			buildScript += "sudo cqlsh -f src/main/resources/config/cql/changelog/00000000000000_create-tables.cql\n";
			buildScript += "sudo cqlsh -f src/main/resources/config/cql/changelog/00000000000001_insert_default_users.cql\n";
		}
		// Juste check that mongodb service is started.
		if(conf.databaseType.equals("mongodb")){
			buildScript += "echo 'MongoDB!'\n";
			buildScript += "sudo service mongodb start\n";
		}

		if(conf.devDatabaseType.equals("oracle") || conf.prodDatabaseType.equals("oracle")){
			
		}
		
		if(conf.devDatabaseType.equals("postgresql") || conf.prodDatabaseType.equals("postgresql")){
			buildScript += "service pgservice start\n";
			buildScript += "psql -U postgres\n";
			buildScript += "create role \"jhipster-fou\" login;\n";
			buildScript += "create database \"jhipster-fou\";\n";
			buildScript += "\\q\n";
		}
		
		if(conf.devDatabaseType.equals("mariadb") || conf.prodDatabaseType.equals("mariadb")){
			buildScript += "service mysql start\n";
			buildScript += "mysql -u root\n";
			buildScript += "SET SESSION sql_mode = 'ANSI';\n";
			buildScript += "create database if not exists \"jhipster-fou\";\n";
			buildScript += "\\q\n";
		}
		
		if(conf.devDatabaseType.equals("mysql") || conf.prodDatabaseType.equals("mysql")){
			buildScript += "service mysql start >> jhipster-database.log\n";
			buildScript += "mysql -u root >> jhipster-database.log\n";
			buildScript += "SET SESSION sql_mode = 'ANSI'; >> jhipster-database.log \n";
			buildScript += "create database if not exists \"jhipster-fou\"; >> jhipster-database.log\n";
			buildScript += "\\q\n";
		}
		
		// Microservices need Jhipster-registry to be running
		if(!conf.applicationType.equals("monolith") & !conf.applicationType.endsWith("App")){
			
		}
		
		if(conf.buildTool.equals("maven")){
			buildScript += "./mvnw >> jhipsterbuild.log 2>&1\n";
		}
		else{
			buildScript += "./gradlew >> jhipsterbuild.log 2>&1\n";
		}
		
		for(String s : conf.testFrameworks){
			switch (s){
				case "gatling":
									break;
				case "cucumber":
									break;
				case "protractor":	
									break;
			}
		}
		
		Files.writeStringIntoFile(getjDirectory(jDirectory) + "buildAndTest.sh", buildScript);
	}
	
	
	/**
	 * Parsing of JHipster's configuration in JSON.
	 * This parsing is to match the expected values of yo-rc.json file. These values vary depending on the type of application.
	 * 
	 * @param jhipsterConf Configuration of JHipster (@see GeneratorJhipsterConfiguration)
	 * @return
	 */
	private String toJSON2(GeneratorJhipsterConfiguration jhipsterConf) {
		Gson gson;
		// If ClientApp or ServerApp, applicationType should not be displayed.
		if(jhipsterConf.generatorJhipster.applicationType.endsWith("App")){gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setPrettyPrinting().create();}
		else {gson = new GsonBuilder().setPrettyPrinting().create();}
		
		//TODO JWT is wrong because no template with JWT
		/*if (!Utils.testJson(gson.toJsonTree(jhipsterConf), new JsonChecker())){
			System.err.println("JSON parsed is wrong !!!");
		}*/
		
		return gson.toJson(jhipsterConf);	
	}

	
	private String falseByNo(String s) {
		if (s.equals("false")) return "no";
		return s;
	}
	
	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return is "ft" in the configuration?
	 */
	private String isIncluded(String ft, Set<String> strConfs) {
		if (strConfs.contains(ft))
			return "true";
		return "false"; 
		
	}

	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return value of "ft" in the configuration... otherwise false
	 */
	private String get(String ft, Set<String> strConfs, FeatureModelVariable fmvJhipster) {
		SetVariable chs = fmvJhipster.getFeature(ft).children();
		 for (Variable ch : chs.getVars()) {
			FeatureVariable ftv = (FeatureVariable) ch;
			String ftName = ftv.getFtName();
			if (strConfs.contains(ftName))
				return toRealFtName2(ftName);
		}
		return "false"; // "no" sometimes
	}
	
	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return values of "ft" in the configuration... (typically OR-groups)
	 */
	private String[] gets(String ft, Set<String> strConfs,
			FeatureModelVariable fmvJhipster) {
		
		ArrayList<String> strs = new ArrayList<String>();
		 SetVariable chs = fmvJhipster.getFeature(ft).children();
		 for (Variable ch : chs.getVars()) {
			FeatureVariable ftv = (FeatureVariable) ch; 
			String ftName = ftv.getFtName();
			if (strConfs.contains(ftName))
				strs.add(toRealFtName2(ftName));
		}
		String[] r = new String[strs.size()];
		return (String[]) strs.toArray(r); 
		 
	}

	/**
	 * Change feature name as presented in feature model to match expected value in yo-rc.json
	 * 
	 * @param feature Name of the feature (as written in the feature model)
	 * @return The name of the feature as expected by JHipster
	 */
	private static String toRealFtName2(String feature){
		switch (feature){
			case "MicroserviceApplication": return "microservice";
			case "MicroserviceGateway":		return "gateway";
			case "Monolithic":				return "monolith";
			case "UaaServer":				return "uaa";
			case "Maven":					return "maven";
			case "Gradle":					return "gradle";
			case "SQL":						return "sql";
			case "Cassandra":				return "cassandra";
			case "MongoDB":					return "mongodb";
			case "Oracle12c":				return "oracle";
			case "PostgreSQLDev":			return "postgresql";
			case "MariaDB":					return "mariadb";
			case "MySql":					return "mysql";
			case "Oracle":					return "oracle";
			case "MariaDBDev":				return "mariadb";
			case "H2DiskBased":				return "h2Disk";
			case "H2InMemory":				return "h2Memory";
			case "ElasticSearch":			return "elasticsearch";
			case "HazelCast":				return "hazelcast";
			case "EhCache":					return "ehcache";
			case "MySQL":					return "mysql";
			case "PostgreSQL":				return "postgresql";
			case "HTTPSession":				return "session";
			case "Uaa":						return "uaa";
			case "OAuth2":					return "oauth2";
			case "JWT":						return "jwt";
			case "Gatling":					return "gatling";
			case "Protractor":				return "protractor";
			case "Cucumber":				return "cucumber";
			case "ServerApp":				return "serverApp";
			default: 	return feature;
		}
	}
	
	/**
	 * Create a directory in the folder {@link #JIHPSTERS_DIRECTORY}.
	 * 
	 * @param jDirectory Name of the folder to create.
	 */
	private void mkdirJhipster(String jDirectory) {
		File dir = new File(getjDirectory(jDirectory));
    	if (!dir.exists()) {
    		_log.info("Creating folder "+jDirectory+" ...");
		    try {dir.mkdir();} 
		    catch(SecurityException se){_log.error(se.getMessage());}        
		}
    	else{_log.info("Folder "+jDirectory+" already exists, no need to create it...");}
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
	 * Extract the features of a specific configuration and return them in an Set of String.
	 * 
	 * @param configuration JHipster's configuration to work on.
	 * @return All features of configuration in a Set of String.
	 */
	private Set<String> extractFeatures(Variable configuration){
		SetVariable svconf = (SetVariable) configuration;
		Set<Variable> fts = svconf.getVars();
		Set<String> strConfs = new HashSet<String>();
		for (Variable ft : fts) {
			FeatureVariable ftv = (FeatureVariable) ft;
			strConfs.add(ftv.getFtName());
		}
		return strConfs;
	}
	
	
	private int nbClauses(Node node) {
		return node.toCNF().getChildren().length;
	}
	
	private void generateDimacs(FeatureModelVariable fmvJhipster){
		String dimacsHipster = fmvJhipster.convert(org.xtext.example.mydsl.fml.FMFormat.DIMACS);
		
		Node n = new SATFMLFormula(fmvJhipster).getNode();
		Files.writeStringIntoFile(DIMACS_FILENAME, dimacsHipster.replace("XXXXXX", "" + nbClauses(n)));
	}
	
	
	@SuppressWarnings("unused")
	@Deprecated
	private void generateClientAppScript(String jDirectory){
		String buildScript = "#!/bin/bash\n\n"
				+ "ofolder=`pwd`\n" 
				+ "yo jhipster:client >> jhipsteryo.log 2>&1\n";
		
		buildScript += "gulp test >> test.log\n";
		
		Files.writeStringIntoFile(getjDirectory(jDirectory) + "buildAndTest.sh", buildScript);
	}
	

	/**
	 * Generates all variants of JHipster 3.6.1 to test them afterwards. 
	 */
	@Test
	public void testJHipsterGeneration() throws Exception{
		
		
		_log.info("Extracting Feature Model...");
		FeatureModelVariable fmvJhipster = getFMJHipster();
		
		_log.info("Checking validity of Feature Model...");
		assertTrue(fmvJhipster.isValid());
		_log.info("Feature Model is valid !");
		
		_log.info("The feature model has: "+fmvJhipster.counting()+" valid configuration(s).");
		_log.info("The feature model has: "+fmvJhipster.nbFeatures()+ " feature(s).");
		_log.info("The feature model has: "+fmvJhipster.cores().size()+" core feature(s).");
		_log.info("The feature model has: "+fmvJhipster.deads().size()+" dead feature(s).");
		_log.info("The feature model has: "+fmvJhipster.falseOptionalFeatures().size()+" false optional feature(s).");
		_log.info("The feature model has: "+fmvJhipster.fullMandatoryFeatures().size()+" full mandatory feature(s).");
		_log.info("The feature model has: "+fmvJhipster.getAllConstraints().size()+" constraint(s).");
		_log.info("The feature model has a depth of "+fmvJhipster.depth());
		
		_log.info("Extracting configurations...");
		Set<Variable> confs = fmvJhipster.configs();
		_log.info("Extraction done !");
		
		_log.info("Generating DIMACS...");
		generateDimacs(getFMJHipster());
		
		int i = 0;
		for (Variable configuration : confs){

			_log.info("Extracting features from the configuration...");
			Set<String> strConfs = extractFeatures(configuration);
			
			JhipsterConfiguration jConf = toJhipsterConfiguration(strConfs, getFMJHipster());
			
			// TODO: Nevermind Oracle, H2, ClientApp & ServerApp for now.
			if(jConf.applicationType.endsWith("App") ||jConf.devDatabaseType.equals("oracle") 
				|| jConf.prodDatabaseType.equals("oracle") || jConf.devDatabaseType.equals("H2") ){}
			else{
				i++;
				String jDirectory = "jhipster" + i;
				mkdirJhipster(jDirectory);
			 	
				_log.info("Parsing JSON...");
	
				GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
				jhipGen.generatorJhipster = jConf;
				String yorc = toJSON2(jhipGen);
				Files.writeStringIntoFile(getjDirectory(jDirectory) + ".yo-rc.json", yorc);
				_log.info("JSON generated...");
				
				_log.info("Generating scripts...");
				new ScriptsBuilder().generateYoJhipsterScript(jConf, jDirectory);
				if (!jConf.applicationType.equals("clientApp"))
				{
					new ScriptsBuilder().generateKillScript(jDirectory);
					new ScriptsBuilder().generateCompileScript(jConf, jDirectory);
					new ScriptsBuilder().generateBuildScript(jConf, jDirectory);
					new ScriptsBuilder().generateTestScript(jConf, jDirectory);
				}
								
				_log.info("Scripts generated...");
		
				_log.info("Configuration "+i+", "+jConf.applicationType+", is done");
				
				if(i==5){
					_log.info("Stopping at 5...");
					System.exit(0);
				}
			}
		}
	}
}
