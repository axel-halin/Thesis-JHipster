import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;
import org.prop4j.Node;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.io.AbstractFeatureModelReader;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelReader;
import fr.familiar.FMLTest;
import fr.familiar.fm.featureide.FeatureIDEtoFML;
import fr.familiar.operations.CountingStrategy;
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
public class FMLTest1 extends FMLTest {
	
	private final static Logger _log = Logger.getLogger("FMLTest1");
	
	private static final String DIMACS_FILENAME = "models/model.dimacs";
	
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	
	//private static final String JHISPTERS_DIRECTORY_DEPLOYMENT = "/Users/macher1/Documents/SANDBOX/jhipsterICSE2";
	private static final String JHISPTERS_DIRECTORY_DEPLOYMENT = "/Dev/JHipster/Documents/SANDBOX/jhipsterICSE2";
	
	@Test
	public void test2() throws Exception {
		TWiseGen tgen = new TWiseGen();
		
		// http://folk.uio.no/martifag/splcatool/
		Map<String, String> argsMap = new HashMap<String, String>();
		argsMap.put("t", "t_wise");
	    argsMap.put("limit", "100%");
	    argsMap.put("a", "ICPL"); // Chvatal
	    argsMap.put("s", "3");
	    argsMap.put("fm", DIMACS_FILENAME);
		
		Config twise = tgen.twise(argsMap);
		twise.printProducts();
		
		Set<Set<String>> confs = twise.getConfs();
		int i = 0;
		for (Set<String> conf : confs) {
			i++; 			
			
			String jDirectory = "jhipster" + "" + i;
			mkdirJhipster(jDirectory);
			
			
			//String yorc = toJSON(conf, getFMJHipster());
			
			
			JhipsterConfiguration jConf = toJhipsterConfiguration(conf, getFMJHipster());
			GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
			jhipGen.generatorJhipster = jConf;
			String yorc = toJSON2(jhipGen, getFMJHipster());
			
			
			Files.writeStringIntoFile(getjDirectory(jDirectory) + ".yo-rc.json", yorc);
			
			// TODO: also write the configuration file because 
			// (1) we will use it afterwards for ML 
			// (2) in case the JSON is not OK 
			// (3) we can "store" it in a kind of database 
			
			/*String buildScript = "#!/bin/bash\n\n"
					+ "ofolder=`pwd`\n" 
					+ "yo jhipster >> jhipsteryo.log 2>&1\n";
			
			if (conf.contains("mongodb")) {
				buildScript += "echo 'Mongodb!'\n";
				buildScript += "mongod --dbpath . &";
			}
			
			if (conf.contains("useCompass")) {
				buildScript += "echo 'Compass activated (CSS) issue here: https://github.com/jhipster/generator-jhipster/issues/952'\n";
			}
			
			if (conf.contains("mysql") || conf.contains("mysqlDev")) {
				buildScript += "echo 'MySQL (some work seems needed: https://github.com/jhipster/generator-jhipster/issues/1271'\n";
			}
			
			if (conf.contains("maven")) {
				buildScript += "mvn spring-boot:run >> jhipsterbuild.log 2>&1\n";
			}
			if (conf.contains("gradle")) {
				buildScript += "gradle bootRun >> jhipsterbuild.log 2>&1\n";
			}*/
			
			//Files.writeStringIntoFile(getjDirectory(jDirectory) + "buildAndTest.sh", buildScript);

		}
		
		
		
	}

	private void mkdirJhipster(String jDirectory) {
		File dir = new File(getjDirectory(jDirectory));
    	if (!dir.exists()) {
		    try {
		        dir.mkdir();
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		}
	}

	private String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}
	
	@Test 
	public void testReverseFunction() {
		
		String str = "PLOP";
		// encode data on your side using BASE64
		byte[]   bytesEncoded = Base64.getEncoder().encode(str.getBytes());
		System.out.println("ecncoded value is " + new String(bytesEncoded ));

		// Decode data on other side, by processing encoded data
		byte[] valueDecoded= Base64.getDecoder().decode(bytesEncoded );
		System.out.println("Decoded value is " + new String(valueDecoded));
	}
	

	@Test
	public void test() throws Exception {
		//FeatureModelVariable fm1 = FM ("A : [B] [C] ; ");
		//assertEquals (4, fm1.counting(), 0.0);
		
		//String strfm = writeToString(new File ("modelJhipster.xml"));
		
		
		/***** merge operations: connection needed with FMCalc *****/
		//gsd.synthesis.FeatureModel<String> nfm = FMBuilder.getInternalFM(strfm);
		//if (nfm == null) {
			//fail("Unable to parse the feature model");			
	//	}

		//FeatureModelVariable fmv = new FeatureModelVariable("fmJhipster", nfm) ;
		//System.err.println("#fmv=" + fmv.counting(CountingStrategy.BDD_FML));
		
		FeatureModelVariable fmvJhipster = getFMJHipster();
		
		System.err.println("#" + fmvJhipster.counting());	// Displays number of possible configurations
		System.err.println("jhipster=" + fmvJhipster);		// Displays variability model (feature model)
		
		System.err.println("#" + fmvJhipster.counting(CountingStrategy.SAT_FEATUREIDE));
		
		//FeatureModel fmIDE = new FMLtoFeatureIDE(fmvJhipster).convert();
		
		String dimacsHipster = fmvJhipster.convert(org.xtext.example.mydsl.fml.FMFormat.DIMACS);
		
		Node n = new SATFMLFormula(fmvJhipster).getNode();
		Files.writeStringIntoFile(DIMACS_FILENAME, dimacsHipster.replace("XXXXXX", "" + nbClauses(n)));
		
		Set<Variable> confs = fmvJhipster.configs();
		int i = 0;
		
		
		for (Variable conf : confs) {
			i++;
			
			SetVariable svconf = (SetVariable) conf;
			Set<Variable> fts = svconf.getVars();
			Set<String> strConfs = new HashSet<String>();
			for (Variable ft : fts) {
				FeatureVariable ftv = (FeatureVariable) ft;
				strConfs.add(ftv.getFtName());
				//System.err.print(ftv.getFtName() + " ; ");
			}
			JhipsterConfiguration jConf = toJhipsterConfiguration(strConfs, fmvJhipster);
			GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
			jhipGen.generatorJhipster = jConf;
			String content = toJSON2(jhipGen, fmvJhipster);
			
			
			
			JsonObject jsonContent = new JsonParser().parse(content).getAsJsonObject();
			assertNotNull(jsonContent);
			
			Gson g = new Gson();
			GeneratorJhipsterConfiguration jhipsterConf = g.fromJson(content, GeneratorJhipsterConfiguration.class);
			assertNotNull(jhipsterConf); // exception can happen
			
			
			String jhipsterConfJSON = g.toJson(jhipsterConf);
		//	_log.info("Good " + i);
		//	_log.info("jsonContent " + jsonContent);
		//	_log.info("jsonContent " + jhipsterConfJSON);
			
			assertTrue(compareJson(jsonContent, new JsonParser().parse(jhipsterConfJSON).getAsJsonObject()));
			
			JhipsterConfiguration jhipsterConf2 = toJhipsterConfiguration(strConfs, fmvJhipster);
			
			_log.info("jhipsterConf " + jhipsterConf.generatorJhipster);
			_log.info("jhipsterConf2 " + jhipsterConf2);
			assertTrue(jhipsterConf.generatorJhipster.equals(jhipsterConf2));
			
			GeneratorJhipsterConfiguration jhipsterGen = new GeneratorJhipsterConfiguration();
			jhipsterGen.generatorJhipster = jhipsterConf2;
			//String content2 = toJSON2(jhipsterGen, fmvJhipster);
				
			//String content3 = toJSON2(jhipsterConf, fmvJhipster);
			//assertNotNull(content2);
			//assertNotNull(content3);
			//TODO Move
		//	String jDirectory = "jhipster" + "" + i;
		//	mkdirJhipster(jDirectory);
			
			//Files.writeStringIntoFile("confs-without-oracle-with-grunt/.yo-rc.json" + i, content);
			// System.err.println("\n====\n");
		}
		System.err.println("i=" + i);
		
	}
	
	@Test
	public void testConfig() throws Exception {
				
		FeatureModelVariable fmvJhipster = getFMJHipster();
		
		System.err.println("#" + fmvJhipster.counting());
		System.err.println("jhipster=" + fmvJhipster);
		
		System.err.println("#" + fmvJhipster.counting(CountingStrategy.SAT_FEATUREIDE));
		
		//FeatureModel fmIDE = new FMLtoFeatureIDE(fmvJhipster).convert();
		
		String dimacsHipster = fmvJhipster.convert(org.xtext.example.mydsl.fml.FMFormat.DIMACS);
		
		Node n = new SATFMLFormula(fmvJhipster).getNode();
		Files.writeStringIntoFile(DIMACS_FILENAME, dimacsHipster.replace("XXXXXX", "" + nbClauses(n)));
		
		Set<Variable> confs = fmvJhipster.configs();
		int i = 0;
		
		for (Variable conf : confs) {
			i++;
			
			SetVariable svconf = (SetVariable) conf;
			Set<Variable> fts = svconf.getVars();
			Set<String> strConfs = new HashSet<String>();
			for (Variable ft : fts) {
				FeatureVariable ftv = (FeatureVariable) ft;
				strConfs.add(ftv.getFtName());
				//System.err.print(ftv.getFtName() + " ; ");
			}
			
			GeneratorJhipsterConfiguration jhipsterGen = new GeneratorJhipsterConfiguration();
			JhipsterConfiguration jhipsterConf = toJhipsterConfiguration(strConfs, fmvJhipster);
			jhipsterGen.generatorJhipster = jhipsterConf;
			assertNotNull(jhipsterConf);
			
			String jSon = toJSON2(jhipsterGen, fmvJhipster);
			assertNotNull(jSon);	
			
			if (i <= 200) {
				// mkdir directory 
				String dirLocation = JHISPTERS_DIRECTORY_DEPLOYMENT + "/" + "jhipster-" + Base64.getEncoder().encode(jhipsterConf.toString().getBytes());
				File dir = new File(dirLocation);
		    	if (!dir.exists()) 
				     dir.mkdir();
				    	
		    	// TODO: tmpconf
		    	String tmpDirLocation = dirLocation + "/" + "tmpconfig";
		    	File tmpDir = new File(tmpDirLocation);
		    	if (!tmpDir.exists()) 
		    		tmpDir.mkdir();
				
				Files.writeStringIntoFile(dirLocation + "/" + ".yo-rc.json", jSon);
				
				Files.writeStringIntoFile(tmpDirLocation + "/" + ".yo-rc.json", jSon);
				Files.writeStringIntoFile(tmpDirLocation + "/" + ".conf", strConfs.toString());
				Files.writeStringIntoFile(tmpDirLocation + "/" + ".jconf", jhipsterConf.toString());
				
			}
		
		}
		
		
	}
	
	@Test
	public void testJSonConfig() {
		
		// basics of JSON
		JsonObject jsonObject = new JsonParser().parse("{\"name\": \"John\"}").getAsJsonObject();
		assertEquals("John", jsonObject.get("name").getAsString()); 	
		
		
		String strFooConfig = 
				"{\n" + 
				"  \"generator-jhipster\": " +
				"{\n" + 
				"    \"baseName\": \"jhipsterICSE\",\n" + 
				"    \"packageName\": \"com.mycompany.myapp\",\n" + 
				"    \"packageFolder\": \"com/mycompany/myapp\",\n" + 
				"    \"authenticationType\": \"session\",\n" + 
				"    \"hibernateCache\": \"hazelcast\",\n" + 
				"    \"clusteredHttpSession\": \"hazelcast\",\n" + 
				"    \"websocket\": \"no\",\n" + 
				"    \"databaseType\": \"sql\",\n" + 
				"    \"devDatabaseType\": \"h2Disk\",\n" + 
				"    \"prodDatabaseType\": \"mysql\",\n" + 
				"    \"searchEngine\": \"no\",\n" + 
				"    \"useSass\": false,\n" + 
				"    \"buildTool\": \"maven\",\n" + 
				//"    \"frontendBuilder\": \"grunt\",\n" + 
				"    \"enableTranslation\": true,\n" + 
				"    \"enableSocialSignIn\": false,\n" + 
				"    \"rememberMeKey\": \"9f1246dacd676048c45ae180a981305f4b995d3e\",\n" + 
				"    \"testFrameworks\": [\n" + 
				"      \"gatling\"\n" + 
				"    ]\n" + 
				"  }\n" + 
				"}\n" + 
				"";
		JsonObject fooConfig = new JsonParser().parse(strFooConfig).getAsJsonObject();
		assertNotNull(fooConfig);
		
		Gson g = new Gson();
		GeneratorJhipsterConfiguration jhipsterConf = g.fromJson(strFooConfig, GeneratorJhipsterConfiguration.class);
		System.err.println("jhipsterConf=" + g.toJson(jhipsterConf));
		
		assertTrue(compareJson(fooConfig, new JsonParser().parse(g.toJson(jhipsterConf)).getAsJsonObject()));
		
		
		
	}
	
	/** http://blog.sodhanalibrary.com/2016/02/compare-json-with-java-using-google.html#.Vz4WG5PRiEI
	 * @param json1
	 * @param json2
	 * @return
	 */
	private boolean compareJson(JsonElement json1, JsonElement json2) {
		        boolean isEqual = true;
		        // Check whether both jsonElement are not null
		        if(json1 !=null && json2 !=null) {
		            
		            // Check whether both jsonElement are objects
		            if (json1.isJsonObject() && json2.isJsonObject()) {
		                Set<Entry<String, JsonElement>> ens1 = ((JsonObject) json1).entrySet();
		                Set<Entry<String, JsonElement>> ens2 = ((JsonObject) json2).entrySet();		              
		                JsonObject json2obj = (JsonObject) json2;
		                if (ens1 != null && ens2 != null && (ens2.size() == ens1.size())) {
		                    // Iterate JSON Elements with Key values
		                    for (Entry<String, JsonElement> en : ens1) {
		                        isEqual = isEqual && compareJson(en.getValue() , json2obj.get(en.getKey()));
		                    }
		                } else {		                
		                    return false;
		                }
		            } 
		            
		            // Check whether both jsonElement are arrays
		            else if (json1.isJsonArray() && json2.isJsonArray()) {
		                JsonArray jarr1 = json1.getAsJsonArray();
		                JsonArray jarr2 = json2.getAsJsonArray();
		                if(jarr1.size() != jarr2.size()) {
		                    return false;
		                } else {
		                    int i = 0;
		                    // Iterate JSON Array to JSON Elements
		                    for (JsonElement je : jarr1) {
		                        isEqual = isEqual && compareJson(je , jarr2.get(i));
		                        i++;
		                    }   
		                }
		            }
		            
		            // Check whether both jsonElement are null
		            else if (json1.isJsonNull() && json2.isJsonNull()) {
		                return true;
		            } 
		            
		            // Check whether both jsonElement are primitives
		            else if (json1.isJsonPrimitive() && json2.isJsonPrimitive()) {
		                return json1.equals(json2);
		            } 
		            else {
		                return false;
		            }
		        } 
		        else if(json1 == null && json2 == null) {
		            return true;
		        } else {		        	
		            return false;
		        }
		        
		        return isEqual;
	    }
		
	

	/**
	 * https://github.com/jhipster/generator-jhipster/blob/master/app/index.js
	 * @return
	 * @throws Exception
	 */
	private FeatureModelVariable getFMJHipster() throws Exception {
		return FM ("jhipster", "FM ( jhipster :  Base ; Base : "
				+ "Application Authentication [Database] [ClusteredSession] [SpringWebSockets] BackEnd [InternationalizationSupport] [TestingFrameworks] ; "
				+ "Application : (Microservice | Monolithic) ; "
				+ "Microservice: ( MicroserviceGateway | MicroserviceServer) ; "
				+ "MicroserviceServer: ( MicroserviceApplication | UaaServer) ; "
				+ "Monolithic: [SocialLogin] AbstractMonolithic ; "
				+ "AbstractMonolithic: [Server] [Client] ; "
				+ "Client : [LibSass] ; "
				+ "Authentication : ( HTTPSession | Uaa | OAuth2 | JWT ) ; "
				+ "Database :  (SQL | Cassandra | MongoDB) ; "
				+ "SQL : Production Development [Hibernate2ndLvlCache] [ElasticSearch] ; "
				+ "Production : (MySQL | Oracle | MariaDB | PostgreSQL) ; "
				+ "Development : (Oracle12c | H2 | PostgreSQLDev | MariaDBDev | MySql) ; "
				+ "H2 : (DiskBased | InMemory) ; "
				+ "Hibernate2ndLvlCache : (HazelCast | EhCache) ; "
				+ "BackEnd : (Gradle | Maven) ; "
				+ "TestingFrameworks : [Cucumber] [Gatling] [Protractor] ; "
				// Constraints
				+ "(OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB) ; "
				+ "(!Server & Client) -> (!Gatling & !Cucumber) ; "
				+ "UaaServer -> Uaa ; "
				+ "H2 -> (DiskBased & !InMemory) | (!DiskBased & InMemory) ; "
				+ "Oracle -> (H2 | Oracle12c) ; "
				+ "(!OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB | Cassandra) ; "
				+ "MySQL -> (H2 | MySql) ; "
				+ "Monolithic -> (JWT | HTTPSession | OAuth2) ; "
				+ "SocialLogin -> (SQL | MongoDB) ; "
				+ "MariaDB -> (H2 | MariaDBDev) ; "
				+ "(MicroserviceServer | (Server | !Client)) -> !Protractor ; "
				+ "PostgreSQL -> (H2 | PostgreSQLDev) ; "
				+ "SocialLogin -> (HTTPSession | JWT) ; "
				+ "(MicroserviceGateway | MicroserviceApplication) -> (JWT | Uaa) ; "
				+ "ClusteredSession -> (Monolithic | MicroserviceGateway) ; "
				+ "SpringWebSockets -> (Monolithic | MicroserviceGateway) ; "
				+ "!Database -> MicroserviceApplication ; "
//				+ "Monolithic -> (Client & !Server) | (!Client & Server) | (Client & Server) ; "
				+ "Microservice -> !Monolithic ; Monolithic -> !Microservice ; "
				+ "MicroserviceGateway -> !MicroserviceServer ; MicroserviceServer -> !MicroserviceGateway ; "
				
/*							
				+ "BackEnd Application [Database] Authentication [ClusteredSession] [InternationalizationSupport] [SpringWebSockets] [TestingFrameworks] ; "
				+ "BackEnd : (Gradle | Maven) ; "
				+ "Application : (Monolithic | MicroserviceGateway | MicroserviceApplication | UaaServer) ; "
				+ "Monolithic : [SocialLogin] [LibSass] ; "
				+ "Database : (SQL | Cassandra | MongoDB) ; "
				+ "SQL : Development Production [Hibernate2ndLvlCache] [ElasticSearch] ; "
				// Abstract feature H2 ??? H2 : DiskBased | InMemory; but H2 is selected not its subfeatures...
				+ "Development : (Oracle12c | H2DiskBased | H2InMemory | PostgreSQLDev | MariaDBDev | MySql | MongoDev | CassandraDev) ; "
				+ "Production : (MySQL | Oracle | MariaDB | PostgreSQL | MongoProd | CassandraProd) ; "
				+ "Hibernate2ndLvlCache : (HazelCast | EhCache) ; "
				+ "Authentication : (HTTPSession | Uaa | OAuth2 | JWT) ; "
				+ "TestingFrameworks : [Gatling] [Cucumber] [Protractor] ; "
				+ "(OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB) ; "
				+ "SocialLogin -> (Monolithic & (HTTPSession | JWT) ) ; "
				+ "UaaServer -> Uaa ; "
				+ "Oracle -> (H2DiskBased | H2InMemory | Oracle12c) ; "
				+ "(!OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB | Cassandra) ; "
				+ "MySQL -> (H2DiskBased | H2InMemory | MySql) ; "
				+ "Monolithic -> (JWT | HTTPSession | OAuth2) ; "
				+ "SocialLogin -> (SQL | MongoDB) ; "
				+ "MariaDB -> (H2DiskBased | H2InMemory | MariaDBDev) ; "
				+ "(MicroserviceApplication | UaaServer) -> !Protractor ; "
				+ "PostgreSQL -> (H2DiskBased | H2InMemory | PostgreSQLDev) ; "
				+ "(MicroserviceGateway | MicroserviceApplication) -> (JWT | Uaa) ; "
				+ "ClusteredSession -> (Monolithic | MicroserviceGateway) ; "
				+ "SpringWebSockets -> (Monolithic | MicroserviceGateway) ; "
				+ "!Database -> MicroserviceApplication ; "
				+ "MongoDB -> MongoDev ; "
				+ "MongoDB -> MongoProd ; "
				+ "Cassandra -> CassandraDev ; "
				+ "Cassandra -> CassandraProd ; "
				+ "SQL -> (!MongoDev & !MongoProd) ;"
				+ "SQL -> (!CassandraDev & !CassandraProd) ; "*/
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
		
		JhipsterConfiguration jhipsterConf = new JhipsterConfiguration();
		
		// Default values
		final String BASENAME = "jhipster-fou";
		final String PACKAGENAME = "io.variability.jhipster";
		final String SERVERPORT = "8080";
		final String JHIPREFIX	= "jhi";
		final String JWTKEY 	= "e2ff997a0a4f7a333044f23f31c5aa5bb7154b95";
		
		// Common properties
		String baseName = BASENAME;
		String packageName = PACKAGENAME;
		String packageFolder = PACKAGENAME.replace(".", "/");
		String serverPort = SERVERPORT;
		String authenticationType = get("Authentication", strConfs, fmvJhipster);
		String hibernateCache = falseByNo(get("Hibernate2ndLvlCache", strConfs, fmvJhipster));
		String databaseType = get("Database", strConfs, fmvJhipster);
		String devDatabaseType = get("Development", strConfs, fmvJhipster);
		String prodDatabaseType = get("Production", strConfs, fmvJhipster);
		String searchEngine = falseByNo(get("ElasticSearch", strConfs, fmvJhipster));
		String buildTool = get("BackEnd", strConfs, fmvJhipster);
		String jwtSecretKey = JWTKEY;
		String[] testsFramework = gets("TestingFrameworks", strConfs, fmvJhipster);
		String jhiPrefix = JHIPREFIX;
		String enableTranslation = isIncluded("InternationalizationSupport", strConfs);
		
		jhipsterConf.serverPort = serverPort;
		jhipsterConf.jhiPrefix = jhiPrefix;
		
		if (authenticationType.equals("JWT")) jhipsterConf.jwtSecretKey = jwtSecretKey;
		
		// Specific properties
		
		String applicationType = get("Application", strConfs, fmvJhipster);
		
		// If Monolith check if client and/or server
		if (applicationType.equals("monolith")){
			if (gets("AbstractMonolithic", strConfs, fmvJhipster).length > 1){
				String sessionSocial = isIncluded("SocialLogin", strConfs);
			//	jhipsterConf.enableSocialSignIn = Boolean.parseBoolean(sessionSocial);
				String clusteredHttpSession = falseByNo(get("ClusteredSession", strConfs, fmvJhipster));
				String websocket = falseByNo(get("SpringWebSockets", strConfs, fmvJhipster));
				jhipsterConf.clusteredHttpSession = clusteredHttpSession;
				jhipsterConf.websocket = websocket;
				String useSass = isIncluded("LibSass", strConfs);
		//		jhipsterConf.useSass = Boolean.parseBoolean(useSass);
			}
			// Check just server or client
		}
		// Else Microservice -> check which one
		else{
			if (get("Microservice", strConfs, fmvJhipster).equals("MicroserviceGateway")) {
				applicationType = "MicroserviceGateway";
				String clusteredHttpSession = falseByNo(get("ClusteredSession", strConfs, fmvJhipster));
				String websocket = falseByNo(get("SpringWebSockets", strConfs, fmvJhipster));
				jhipsterConf.clusteredHttpSession = clusteredHttpSession;
				jhipsterConf.websocket = websocket;
			}
			else applicationType = get("MicroserviceServer", strConfs, fmvJhipster);
		}
		
		

		
		
		

		
//		String frontEndBuilder = get("Client", strConfs, fmvJhipster);



		
		
		
		
		
		jhipsterConf.baseName = baseName;
		jhipsterConf.packageName = packageName;
		jhipsterConf.packageFolder = packageFolder;
		jhipsterConf.authenticationType = authenticationType;
		
		
	/*	for (int i =0; i<applicationType.length; i++){System.err.println(applicationType[i]);}
		
		if (applicationType.length > 1) {jhipsterConf.applicationType = "monolith";}
		else{ System.err.println("Stop!!!"); jhipsterConf.applicationType = applicationType[0];}*/
		
		


		jhipsterConf.databaseType = databaseType;
		if (jhipsterConf.databaseType.equals("sql")){
			jhipsterConf.devDatabaseType = devDatabaseType;
			jhipsterConf.prodDatabaseType = prodDatabaseType;
			jhipsterConf.searchEngine = searchEngine;
			jhipsterConf.hibernateCache = hibernateCache;
		} else{
			jhipsterConf.devDatabaseType = jhipsterConf.databaseType;
			jhipsterConf.prodDatabaseType = jhipsterConf.databaseType;
			jhipsterConf.searchEngine = "no";
			jhipsterConf.hibernateCache = "no";
		}


		jhipsterConf.buildTool = buildTool;
		//jhipsterConf.frontendBuilder = frontEndBuilder;
	//	jhipsterConf.enableTranslation = Boolean.parseBoolean(enableTranslation);

		jhipsterConf.testFrameworks = testsFramework;
		// footer
		// return header + content + footer;
		return jhipsterConf;
	}
	
	

	private String toJSON2(GeneratorJhipsterConfiguration jhipsterConf, FeatureModelVariable fmvJhipster) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jhipsterConf);	
		
	}
	
	@Deprecated
	private String toJSON3(JhipsterConfiguration jhipsterConf, FeatureModelVariable fmvJhipster) {
	
		String baseName = quote(jhipsterConf.baseName);
		String packageName = quote(jhipsterConf.packageName);
		String packageFolder = quote(jhipsterConf.packageName.replace(".", "/"));
		String authenticationType = quote(jhipsterConf.authenticationType);
		String hibernateCache = quote(jhipsterConf.hibernateCache);
		String clusteredHttpSession = quote(jhipsterConf.clusteredHttpSession);
		String websocket = quote(jhipsterConf.websocket);
		String databaseType = quote(jhipsterConf.databaseType);
		String devDatabaseType = quote(jhipsterConf.devDatabaseType);
		String prodDatabaseType = quote(jhipsterConf.prodDatabaseType);
		String searchEngine = quote(jhipsterConf.searchEngine);
		//String useSass = jhipsterConf.useSass ? "true" : "false";
		String buildTool = quote(jhipsterConf.buildTool);
		//String frontEndBuilder = quote(jhipsterConf.frontendBuilder);
	//	String enableTranslation = jhipsterConf.enableTranslation ? "true" : "false";
	//	String sessionSocial = jhipsterConf.enableSocialSignIn ? "true" : "false";
		
		/* TODO
		 * String testFrameworks = "";
		String[] tsts = jhipsterConf.testFrameworks;
		if (tsts != null && tsts.length > 0) {
			for (int i = 0; i < tsts.length; i++) {
				testFrameworks = tsts[i] + " ";
			}
		}*/
	
		String header = "{\n\t" + "\"generator-jhipster\": {\n" ;
		String content = "\t\t" + quote("baseName") + ": " + baseName + "," + "\n" 
		+ "\t\t" + quote("packageName") + ": " + packageName + "," + "\n"
		+ "\t\t" + quote("packageFolder") + ": " + packageFolder + "," + "\n"
		+ "\t\t" + quote("Authentication") + ": " + authenticationType + "," + "\n"
		+ "\t\t" + quote("Hibernate2ndLvlCache") + ": " + hibernateCache + "," + "\n"
		+ "\t\t" + quote("ClusteredSession") + ": " + clusteredHttpSession + "," + "\n"
		+ "\t\t" + quote("SpringWebSocket") + ": " + websocket + "," + "\n" // "no"
		+ "\t\t" + quote("Database") + ": " + databaseType + "," + "\n" 
		+ "\t\t" + quote("Development") + ": " + devDatabaseType + "," + "\n" 
		+ "\t\t" + quote("Production") + ": " + prodDatabaseType + "," + "\n" 
		+ "\t\t" + quote("ElasticSearch") + ": " + searchEngine + "," + "\n" 
		// useSass at some points
		//+ "\t\t" + quote("LibSass") + ": " + useSass + "," + "\n" // optional feature without children (no need to quote when its false/true) 
		+ "\t\t" + quote("BackEnd") + ": " + buildTool + "," + "\n" 
		//+ "\t\t" + quote("frontendBuilder") + ": " + frontEndBuilder + "," + "\n" 
	// Java 7 deprecated in 2.27	+ "\t\t" + quote("javaVersion") + ": " + quote(get("javaVersion", strConfs, fmvJhipster)) + "," + "\n" 
	//	+ "\t\t" + quote("InternationalizationSupport") + ": " + enableTranslation + ",\n"  // optional feature without children
	//	+ "\t\t" + quote("SocialLogin") + ": " + sessionSocial + ""  // optional feature without children
		;
			
		String footer = "\n\t}\n}";
		
		// footer
		return header + content + footer;
	}

	@Deprecated
	private String toJSON(Set<String> strConfs, FeatureModelVariable fmvJhipster) {
		// header
		final String BASENAME = "jhipster-fou";
		final String PACKAGENAME = "io.variability.jhipster";
		
		String baseName = quote(BASENAME);
		String packageName = quote(PACKAGENAME);
		String packageFolder = quote(PACKAGENAME.replace(".", "/"));
		String authenticationType = quote(get("Authentication", strConfs, fmvJhipster));
		String hibernateCache = quote(falseByNo(get("Hibernate2ndLvlCache", strConfs, fmvJhipster)));
		String clusteredHttpSession = quote(falseByNo(get("ClusteredSession", strConfs, fmvJhipster)));
		String websocket = quote(falseByNo(get("SpringWebSockets", strConfs, fmvJhipster)));
		String databaseType = quote(get("Database", strConfs, fmvJhipster));
		String devDatabaseType = quote(get("Development", strConfs, fmvJhipster));
		String prodDatabaseType = quote(get("Production", strConfs, fmvJhipster));
		String searchEngine = quote(falseByNo(get("ElasticSearch", strConfs, fmvJhipster)));
		String useSass = isIncluded("LibSass", strConfs);
		String buildTool = quote(get("BackEnd", strConfs, fmvJhipster));
		//String frontEndBuilder = quote(get("Client", strConfs, fmvJhipster));
		String enableTranslation = isIncluded("InternationalizationSupport", strConfs);
		String sessionSocial = isIncluded("SocialLogin", strConfs);
		
		// 

		String header = "{\n\t" + "\"generator-jhipster\": {\n" ;
		String content = "\t\t" + quote("baseName") + ": " + baseName + "," + "\n" 
		+ "\t\t" + quote("packageName") + ": " + packageName + "," + "\n"
		+ "\t\t" + quote("packageFolder") + ": " + packageFolder + "," + "\n"
		+ "\t\t" + quote("Authentication") + ": " + authenticationType + "," + "\n"
		+ "\t\t" + quote("Hibernate2ndLvlCache") + ": " + hibernateCache + "," + "\n"
		+ "\t\t" + quote("ClusteredSession") + ": " + clusteredHttpSession + "," + "\n"
		+ "\t\t" + quote("SpringWebSockets") + ": " + websocket + "," + "\n" // "no"
		+ "\t\t" + quote("Database") + ": " + databaseType + "," + "\n" 
		+ "\t\t" + quote("Development") + ": " + devDatabaseType + "," + "\n" 
		+ "\t\t" + quote("Production") + ": " + prodDatabaseType + "," + "\n" 
		+ "\t\t" + quote("ElasticSearch") + ": " + searchEngine + "," + "\n" 
		// useSass at some points
		+ "\t\t" + quote("LibSass") + ": " + useSass + "," + "\n" // optional feature without children (no need to quote when its false/true) 
		+ "\t\t" + quote("BackEnd") + ": " + buildTool + "," + "\n" 
		//+ "\t\t" + quote("frontendBuilder") + ": " + frontEndBuilder + "," + "\n" 
	// Java 7 deprecated in 2.27	+ "\t\t" + quote("javaVersion") + ": " + quote(get("javaVersion", strConfs, fmvJhipster)) + "," + "\n" 
		+ "\t\t" + quote("InternationalizationSupport") + ": " + enableTranslation + ",\n"  // optional feature without children
		+ "\t\t" + quote("SocialLogin") + ": " + sessionSocial + ""  // optional feature without children
		
		;
			
		String footer = "\n\t}\n}";
		
		// footer
		return header + content + footer;
	}

	private String falseByNo(String s) {
		if (s.equals("false"))
			return "no";
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
		 
		//System.err.println("get: ");
		
		
		 SetVariable chs = fmvJhipster.getFeature(ft).children();
		 for (Variable ch : chs.getVars()) {
			FeatureVariable ftv = (FeatureVariable) ch; 
			String ftName = ftv.getFtName();
			
			//System.err.println(ftName);
			
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
				//strs.add(toRealFtName(ftName));
				strs.add(toRealFtName2(ftName));
		}
		String[] r = new String[strs.size()];
		return (String[]) strs.toArray(r); 
		 
	}

	@Deprecated
	private static String toRealFtName(String ftName) {
		if(ftName.equals("hazelcastCluster")) 
			return "hazelcast";
		if(ftName.equals("java7")) 
			return "7";
		if(ftName.equals("java8")) 
			return "8";
		// mysqlDev |  oracleDev |  postgresqlDev
		if(ftName.equals("mysqlDev")) 
			return "mysql";
		if(ftName.equals("oracleDev")) 
			return "oracle";
		if(ftName.equals("postgresqlDev")) 
			return "postgresql";
		
		if(ftName.equals("mongodbProd")) 
			return "mongodb";
		if(ftName.equals("mongodbDev")) 
			return "mongodb";
		
		if(ftName.equals("cassandraDev"))
			return "cassandra";
		if (ftName.equals("cassandraProd"))
			return "cassandra";
		
		
		
		if(ftName.equals("springwebsocket"))
			return "spring-websocket";
		
		return ftName;
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
			default: 	return feature;
		}
	}
	
	
	
	
	private String quote(String s) {
		return "\"" + s + "\"";
	}

	private int nbClauses(Node node) {
		return node.toCNF().getChildren().length;
	}

	
	
	
	
	private static FeatureModel parseFeatureModel(File file) {
		FeatureModel fmFeatureIDE = new FeatureModel();
		AbstractFeatureModelReader fmR = new XmlFeatureModelReader(fmFeatureIDE);
		try {
			fmR.readFromFile(file);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find the file " + file);
			return null ;
		} catch (UnsupportedModelException e) {
			System.err.println("Unable to parse the file (FeatureIDE) "+ file + " " + e);
			return null ;
		}
		return fmFeatureIDE ; 
	}

	private String writeToString(File f) {
		FeatureModel fmFeatureIDE = parseFeatureModel(f) ;
		System.err.println("fmIDE=" + fmFeatureIDE);

		// FIXME @FeatureIDE 
		FeatureIDEtoFML toFML = new FeatureIDEtoFML(fmFeatureIDE);
		return toFML.writeToString();
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
		
		_log.info("Nr of constraints: "+ fmvJhipster.getAllConstraints().size());
		_log.info(fmvJhipster.counting());
		
		fmvJhipster.cleanup();
		//fmvJhipster.cleanup2();
		
		_log.info(fmvJhipster.cores().size());
		_log.info(fmvJhipster.configs().size());
		_log.info(fmvJhipster.deads().size());
		_log.info(fmvJhipster.nbFeatures());
		_log.info(fmvJhipster.falseOptionalFeatures().size());
		_log.info(fmvJhipster.fullMandatoryFeatures().size());
		_log.info(fmvJhipster.depth());
		
		int i = 0;
		for (Variable configuration : fmvJhipster.configs()){
			//System.out.println(configuration.getIdentifier());
			i++;
			
			String jDirectory = "jhipster" + "" + i;
			mkdirJhipster(jDirectory);
			
			
			JhipsterConfiguration jConf = toJhipsterConfiguration(fmvJhipster.features().names(), getFMJHipster());
			GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
			jhipGen.generatorJhipster = jConf;
			String yorc = toJSON2(jhipGen, getFMJHipster());
			
			Files.writeStringIntoFile(getjDirectory(jDirectory) + ".yo-rc.json", yorc);
			System.err.println(i+" is done");
		}
		
	}
}
