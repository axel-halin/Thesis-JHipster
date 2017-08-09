package r;

import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;

import com.ibm.icu.util.LocaleData.MeasurementSystem;

import prefuse.util.MathLib;

public class Sampling {

	private static Rengine re;
	private static final int NUMBER_OF_RANDOMS = 1000;
	// One-enabled & one-disabled randoms faults/failures
	private static int[] ONE_ENABLED_RANDOMS_FAULTS = new int[NUMBER_OF_RANDOMS]; 
	private static int[] ONE_ENABLED_RANDOMS_FAILURES = new int[NUMBER_OF_RANDOMS];
	private static int[] ONE_DISABLED_RANDOMS_FAULTS = new int[NUMBER_OF_RANDOMS];
	private static int[] ONE_DISABLED_RANDOMS_FAILURES = new int[NUMBER_OF_RANDOMS];
	// One-enabled & one-disabled first one
	private static int ONE_ENABLED_FIRST_ONE_FAULTS = 0;
	private static int ONE_DISABLED_FIRST_ONE_FAULTS = 0;
	// 
	private static int MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS = 0;
	private static int[] MOST_ENABLED_DISABLED_RANDOMS_FAULTS = new int[NUMBER_OF_RANDOMS];
	private static int[] MOST_ENABLED_DISABLED_RANDOMS_FAILURES = new int[NUMBER_OF_RANDOMS];
	
	public static void main(String[] args) {

		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");

		re= new Rengine(args, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			System.exit(1);
		}
		
		RAnalysis.readCSV(re,"jhipsterFeatures.csv","data");
		System.out.println("File read");
		
		//oneEnabled(re);
		//oneDisabled(re);
		mostEnabledDisabled(re);
		
		re.end();
		System.out.println("done");
	}

	
	

	/**
	 *	One-enabled: activate one feature, take first solution with most disabled features
	 * 	==> disable all optional features
	 * 
	 *  
	 *  select all rows where feature is enabled (grep)
	 *  	select all rows where optional features are deactivated (no, false, nd,   
	 * 
	 * @param re
	 */
	private static void oneEnabled(Rengine re){
		getOneEnabled(re,"Docker","true","Docker");
		getOneEnabled(re,"applicationType","monolith","Monolith");
		getOneEnabled(re,"applicationType","gateway","Gateway");
		getOneEnabled(re,"applicationType","microservice","Microservice");
		getOneEnabled(re,"applicationType","uaa","UaaServer");
		// Authentication Type
		getOneEnabled(re,"authenticationType", "jwt", "JWT");
		getOneEnabled(re,"authenticationType", "oauth2", "OAuth2");
		getOneEnabled(re,"authenticationType", "session", "Session");
		getOneEnabled(re,"authenticationType", "uaa", "UAAAuthentication");
		// Hibernate Cache
		getOneEnabled(re,"hibernateCache", "ehcache", "EhCache");
		getOneEnabled(re,"hibernateCache", "hazelcast", "HazelCast");
		// Clustered Session
		getOneEnabled(re,"clusteredHttpSession","hazelcast", "ClusteredSession");
		// Web Socket
		getOneEnabled(re,"websocket","spring-websocket","WebSocket");
		// Database Type
		getOneEnabled(re,"databaseType","cassandra","Cassandra");
		getOneEnabled(re,"databaseType","mongodb","Mongo");
		getOneEnabled(re,"databaseType","sql","SQL");
		// Dev Database Type
		getOneEnabled(re,"devDatabaseType","cassandra","CassandraDev");
		getOneEnabled(re,"devDatabaseType","DiskBased","H2DiskDev");
		getOneEnabled(re,"devDatabaseType","InMemory","H2MemDev");
		getOneEnabled(re,"devDatabaseType","mariadb","MariaDBDev");
		getOneEnabled(re,"devDatabaseType","mongodb","MongoDev");
		getOneEnabled(re,"devDatabaseType","mysql","MySQLDev");
		getOneEnabled(re,"devDatabaseType","postgresql","PostgreDev");
		// Prod Database Type
		getOneEnabled(re,"devDatabaseType","cassandra","CassandraProd");
		getOneEnabled(re,"devDatabaseType","mariadb","MariaDBProd");
		getOneEnabled(re,"devDatabaseType","mongodb","MongoProd");
		getOneEnabled(re,"devDatabaseType","mysql","MySQLProd");
		getOneEnabled(re,"devDatabaseType","postgresql","PostgreProd");
		// Build Tool
		getOneEnabled(re,"buildTool","maven","Maven");
		getOneEnabled(re,"buildTool","gradle","Gradle");
		// Search Engine
		getOneEnabled(re,"searchEngine","elasticsearch","SearchEngine");
		// Social Sign In
		getOneEnabled(re,"enableSocialSignIn","true","SocialSignIn");
		// Use Sass
		getOneEnabled(re,"useSass","true","UseSass");
		// Translation
		getOneEnabled(re,"enableTranslation","true","Translation");

		//aggregate
		for(int i=0; i<NUMBER_OF_RANDOMS;i++)
			aggregateRandom(re,i,"Enabled");
		aggregateFirstOne(re,"Enabled");
		
		extractRandomsStats("Enabled");
	}
	
	
	/**
	 * 
	 * @param re
	 */
	private static void oneDisabled(Rengine re){
		getOneDisabled(re,"Docker","true","Docker");
		// Application Type
		getOneDisabled(re,"applicationType","monolith","Monolith");
		getOneDisabled(re,"applicationType","gateway","Gateway");
		getOneDisabled(re,"applicationType","microservice","Microservice");
		getOneDisabled(re,"applicationType","uaa","UaaServer");
		// Authentication Type
		getOneDisabled(re,"authenticationType", "jwt", "JWT");
		getOneDisabled(re,"authenticationType", "oauth2", "OAuth2");
		getOneDisabled(re,"authenticationType", "session", "Session");
		getOneDisabled(re,"authenticationType", "uaa", "UAAAuthentication");
		// Hibernate Cache
		getOneDisabled(re,"hibernateCache", "ehcache", "EhCache");
		getOneDisabled(re,"hibernateCache", "hazelcast", "HazelCast");
		// Clustered Session
		getOneDisabled(re,"clusteredHttpSession","hazelcast", "ClusteredSession");
		// Web Socket
		getOneDisabled(re,"websocket","spring-websocket","WebSocket");
		// Database Type
		getOneDisabled(re,"databaseType","cassandra","Cassandra");
		getOneDisabled(re,"databaseType","mongodb","Mongo");
		getOneDisabled(re,"databaseType","sql","SQL");
		// Dev Database Type
		getOneDisabled(re,"devDatabaseType","cassandra","CassandraDev");
		getOneDisabled(re,"devDatabaseType","DiskBased","H2DiskDev");
		getOneDisabled(re,"devDatabaseType","InMemory","H2MemDev");
		getOneDisabled(re,"devDatabaseType","mariadb","MariaDBDev");
		getOneDisabled(re,"devDatabaseType","mongodb","MongoDev");
		getOneDisabled(re,"devDatabaseType","mysql","MySQLDev");
		getOneDisabled(re,"devDatabaseType","postgresql","PostgreDev");
		// Prod Database Type
		getOneDisabled(re,"devDatabaseType","cassandra","CassandraProd");
		getOneDisabled(re,"devDatabaseType","mariadb","MariaDBProd");
		getOneDisabled(re,"devDatabaseType","mongodb","MongoProd");
		getOneDisabled(re,"devDatabaseType","mysql","MySQLProd");
		getOneDisabled(re,"devDatabaseType","postgresql","PostgreProd");
		// Build Tool
		getOneDisabled(re,"buildTool","maven","Maven");
		getOneDisabled(re,"buildTool","gradle","Gradle");
		// Search Engine
		getOneDisabled(re,"searchEngine","elasticsearch","SearchEngine");
		// Social Sign In
		getOneDisabled(re,"enableSocialSignIn","true","SocialSignIn");
		// Use Sass
		getOneDisabled(re,"useSass","true","UseSass");
		// Translation
		getOneDisabled(re,"enableTranslation","true","Translation");

		// Aggregate
		for(int i=0; i<NUMBER_OF_RANDOMS;i++)
			aggregateRandom(re,i,"Disabled");
		aggregateFirstOne(re,"Disabled");
		
		extractRandomsStats("Disabled");
	}
	
	
	private static void mostEnabledDisabled(Rengine re){
		re.eval("maxEnabledFeatures <- as.numeric(as.character((data.frame(table(data$nbFeatures))[nrow(data.frame(table(data$nbFeatures))),1])))");
		re.eval("minEnabledFeatures <- as.numeric(as.character((data.frame(table(data$nbFeatures))[1,1])))");
		
		re.eval("mostEnabled <- data[grep(paste(\"\\\\b\",maxEnabledFeatures,\"\\\\b\",sep=\"\"),data$nbFeatures),]");
		re.eval("mostDisabled <- data[grep(paste(\"\\\\b\",minEnabledFeatures,\"\\\\b\",sep=\"\"),data$nbFeatures),]");
		
		// Select first one
		re.eval("mostEnabledDisabledFirstOne <- rbind(mostEnabled[1,],mostDisabled[1,])");
		
		extractBugProportion(re,"mostEnabledDisabledFirstOne",0,"mostEnabledDisabledFirstOne");
		
		System.out.println("************************");
		System.out.println("most-enabled-disabled (first-one):");
		System.out.println("Sample size: "+re.eval("nrow(mostEnabledDisabledFirstOne)").asInt());
		System.out.println("Failures: "+re.eval("data.frame(table(mostEnabledDisabledFirstOne$Build))[1,1]").asInt());
		System.out.println("Faults: "+MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS);
		
		
		// Select randoms
		for(int i =0; i<NUMBER_OF_RANDOMS;i++){
			re.eval(String.format("randomMostEnabled%s <- mostEnabled[sample(nrow(mostEnabled),1),]", i));
			re.eval(String.format("randomMostDisabled%s <- mostDisabled[sample(nrow(mostDisabled),1),]", i));
			re.eval(String.format("randomMostEnabledMostDisabled%s <- rbind(randomMostEnabled%s, randomMostDisabled%s)", i,i,i));
			extractBugProportion(re,"randomMostEnabledMostDisabled"+i,i,"mostEnabledDisabledRandom");
			extractFailures(re, "randomMostEnabledMostDisabled"+i, i, "mostEnabledDisabled");
		}
		
		System.out.println("************************");
		System.out.println("most-enabled-disabled (randoms):");
		System.out.println("Sample size: "+re.eval("nrow(randomMostEnabledMostDisabled0)").asInt());
		//TODO extract failures
		re.assign("mostEnabledDisabledRandomsFailures", MOST_ENABLED_DISABLED_RANDOMS_FAILURES);
		System.out.print("Failures: "+re.eval("mean(mostEnabledDisabledRandomsFailures)").asDouble());
		System.out.println(" ( "+re.eval("sd(mostEnabledDisabledRandomsFailures)").asDouble()+" ) ");
		System.out.println("Min: "+re.eval("min(mostEnabledDisabledRandomsFailures)").asInt()+ "Max: "+re.eval("max(mostEnabledDisabledRandomsFailures)").asInt());
		re.assign("mostEnabledDisabledRandomsFaults", MOST_ENABLED_DISABLED_RANDOMS_FAULTS);
		System.out.print("Faults: "+re.eval("mean(mostEnabledDisabledRandomsFaults)").asDouble());
		System.out.println(" ( "+re.eval("sd(mostEnabledDisabledRandomsFaults)").asDouble()+" ) ");
		System.out.println("Min: "+re.eval("min(mostEnabledDisabledRandomsFaults)").asInt()+ "Max: "+re.eval("max(mostEnabledDisabledRandomsFaults)").asInt());
	}
	
	
	/**
	 * Select configurations matching one-enabled criteria for a specific feature
	 * 
	 * @param re
	 * @param featureType
	 * @param featureName
	 * @param collectionName
	 */
	private static void getOneEnabled(Rengine re, String featureType, String featureName, String collectionName){
		re.eval(String.format("%sOneEnabled <- data[grep(\"%s\", data$%s),]", collectionName, featureName, featureType));

		re.eval(String.format("min%sEnabledFeatures <- as.numeric(as.character((data.frame(table(%sOneEnabled$nbFeatures))[1,1])))", collectionName, collectionName));
		
		re.eval(String.format("%sOneEnabled <- %sOneEnabled[grep(min%sEnabledFeatures, %sOneEnabled$nbFeatures),]", collectionName, collectionName, collectionName, collectionName));

		re.eval(String.format("firstOne%sOneEnabled <- %sOneEnabled[1,]", collectionName, collectionName));
		
		for(int i=0; i<NUMBER_OF_RANDOMS; i++)
			re.eval(String.format("random%sOneEnabled%d <- %sOneEnabled[sample(nrow(%sOneEnabled),1),]", collectionName, i, collectionName, collectionName));
	}
	
	
	private static void getOneDisabled(Rengine re, String featureType, String featureName, String collectionName){
		// If optional features
		if(featureType.equals("Docker") || featureType.equals("hibernateCache")
				|| featureType.equals("clusteredHttpSession") || featureType.equals("websocket")
				|| featureType.equals("searchEngine") || featureType.equals("enableSocialSignIn") 
				|| featureType.equals("useSass") || featureType.equals("enableTranslation"))
		{
			re.eval(String.format("%sOneDisabled <- data[grep(\"no|false|ND\", data$%s),]", collectionName, featureType));
		} else{
			re.eval(String.format("%sOneDisabled <- data[-grep(\"%s\", data$%s),]", collectionName, featureName, featureType));
		}
		
		
		re.eval(String.format("max%sEnabledFeatures <- as.numeric(as.character((data.frame(table(%sOneDisabled$nbFeatures))[nrow(data.frame(table(%sOneDisabled$nbFeatures))),1])))", collectionName, collectionName, collectionName));
		
		re.eval(String.format("%sOneDisabled <- %sOneDisabled[grep(max%sEnabledFeatures, %sOneDisabled$nbFeatures),]", collectionName, collectionName, collectionName, collectionName));
		
		re.eval(String.format("firstOne%sOneDisabled <- %sOneDisabled[1,]", collectionName, collectionName));
		
		for(int i=0; i<NUMBER_OF_RANDOMS; i++)
			re.eval(String.format("random%sOneDisabled%d <- %sOneDisabled[sample(nrow(%sOneDisabled),1),]", collectionName, i, collectionName, collectionName));
	}
	
	
	
	/**
	 * Extract random one-enabled into oneEnabledRandom data.frame
	 * 
	 * @param re
	 */
	private static void aggregateRandom(Rengine re, int number, String method){
		re.eval("one"+method+"Random"+number+" <- rbind(randomDockerOne"+method+number+", randomMonolithOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomGatewayOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMicroserviceOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomUaaServerOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomJWTOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomOAuth2One"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomSessionOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomUAAAuthenticationOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomEhCacheOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomHazelCastOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomClusteredSessionOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomWebSocketOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomCassandraOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMongoOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomSQLOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomCassandraDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomH2DiskDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomH2MemDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMariaDBDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMongoDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMySQLDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomPostgreDevOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomCassandraProdOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMariaDBProdOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMongoProdOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMySQLProdOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomPostgreProdOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomMavenOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomGradleOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomSearchEngineOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomSocialSignInOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomUseSassOne"+method+number+")");
		re.eval("one"+method+"Random"+number+" <- rbind(one"+method+"Random"+number+", randomTranslationOne"+method+number+")");
		
		extractBugProportion(re,"one"+method+"Random"+number,number, method);
		
		extractFailures(re,"one"+method+"Random"+number,number,method);
		
		//re.eval("write.csv(one"+method+"Random"+number+", file = \"one"+method+"Random"+number+".csv\",row.names=TRUE, na=\"\")");
	}
	
	/**
	 * Extract first one one-enabled in oneEnabledFirstOne data.frame
	 * 
	 * @param re
	 */
	private static void aggregateFirstOne(Rengine re, String method){
		re.eval("one"+method+"FirstOne <- rbind(firstOneDockerOne"+method+", firstOneMonolithOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneGatewayOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMicroserviceOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneUaaServerOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneJWTOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneOAuth2One"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneSessionOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneUAAAuthenticationOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneEhCacheOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneHazelCastOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneClusteredSessionOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneWebSocketOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneCassandraOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMongoOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneSQLOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneCassandraDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneH2DiskDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneH2MemDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMariaDBDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMongoDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMySQLDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOnePostgreDevOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneCassandraProdOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMariaDBProdOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMongoProdOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMySQLProdOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOnePostgreProdOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneMavenOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneGradleOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneSearchEngineOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneSocialSignInOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneUseSassOne"+method+")");
		re.eval("one"+method+"FirstOne <- rbind(one"+method+"FirstOne, firstOneTranslationOne"+method+")");

		extractBugProportion(re,"one"+method+"FirstOne",1,method);
		
		System.out.println("**************************************");
		System.out.println("one"+method+"FirstOne:");
		System.out.println("Sample size: "+re.eval("nrow(one"+method+"FirstOne)").asInt());
		System.out.println("Failures: "+re.eval("data.frame(table(one"+method+"FirstOne$Build))[1,2]").asInt());
		System.out.println("Faults: "+ (method.equals("Enabled") ? ONE_ENABLED_FIRST_ONE_FAULTS : ONE_DISABLED_FIRST_ONE_FAULTS));
		
		//re.eval("write.csv(one"+method+"FirstOne, file = \"one"+method+"FirstOne.csv\",row.names=TRUE, na=\"\")");
	}

	
	
	
	private static void extractBugProportion(Rengine re, String collection, int i, String method){
		String regex6 = "SocialUserConnection";
		String regex3 = "Error parsing reference: \\\"jhipster - jhipster-mariadb\\\" is not a valid repository/tag"
						+ "|Error parsing reference: \\\"jhipster - jhipster-mariadb:mariadb - jhipster-registry\\\" is not a valid repository/tag";
		String regex1 = "java.lang.RuntimeException: Failed to get driver instance for jdbcUrl=jdbc:mariadb://mariadb:3306/jhipster"
						+"|java.lang.RuntimeException: Failed to get driver instance for jdbcUrl=jdbc:mariadb://localhost:3306/jhipster";
		String regex2 = "java.lang.IllegalStateException: No instances available for uaa";
		String regex5 = "JdbcTokenStore";
		
		// Bug 1: mariadb gradle
		String bug1Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex1,collection);
		int bug1 = re.eval(bug1Command).asInt();
		// Bug 2: uaa docker
		String bug2Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),][grep(\"true\",%s[grep(\"%s\",%s$Log.Build),]$Docker),])", collection,regex2,collection,collection,regex2,collection);
		int bug2 = re.eval(bug2Command).asInt();
		// Bug 3: mariadb Docker
		String bug3Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex3,collection);
		//String bug3Command = String.format("nrow(%s[grep(\"%s\",%s$V25),])", collection,regex3,collection);
		int bug3 = re.eval(bug3Command).asInt();
		// Bug 5: oauth2
		String bug5Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex5,collection);;
		int bug5 = re.eval(bug5Command).asInt();
		// Bug 6: social login
		String bug6Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Compile),])",collection,regex6,collection);
		int bug6 = re.eval(bug6Command).asInt();
		// Bug 7: uaa not Docker
		String bug7Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),][grep(\"false\",%s[grep(\"%s\",%s$Log.Build),]$Docker),])", collection,regex2,collection,collection,regex2,collection);
		int bug7 = re.eval(bug7Command).asInt();
		
		if(method.startsWith("mostEnabledDisabled")){
			if(collection.endsWith("FirstOne")){
				if(bug1>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
				if(bug2>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
				if(bug3>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
				if(bug5>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
				if(bug6>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
				if(bug7>0) MOST_ENABLED_DISABLED_FIRST_ONE_FAULTS++;
			} else{
				if(bug1>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;
				if(bug2>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;
				if(bug3>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;
				if(bug5>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;
				if(bug6>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;
				if(bug7>0) MOST_ENABLED_DISABLED_RANDOMS_FAULTS[i]++;				
			}
		} else{
			// First-one
			if(collection.endsWith("FirstOne")){
				// Enabled
				if (method.equals("Enabled")){
					if(bug1>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
					if(bug2>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
					if(bug3>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
					if(bug5>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
					if(bug6>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
					if(bug7>0) ONE_ENABLED_FIRST_ONE_FAULTS++;
				} else{
					// Disabled
					if(bug1>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
					if(bug2>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
					if(bug3>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
					if(bug5>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
					if(bug6>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
					if(bug7>0) ONE_DISABLED_FIRST_ONE_FAULTS++;
				}
			} else{
				// Randoms
				if(method.equals("Enabled")){
					// Enabled
					if(bug1>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
					if(bug2>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
					if(bug3>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
					if(bug5>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
					if(bug6>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
					if(bug7>0) ONE_ENABLED_RANDOMS_FAULTS[i]++;
				} else{
					// Disabled
					if(bug1>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
					if(bug2>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
					if(bug3>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
					if(bug5>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
					if(bug6>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
					if(bug7>0) ONE_DISABLED_RANDOMS_FAULTS[i]++;
				}
			}
		}
		
/*		System.out.println("Number of bug 1: "+bug1);
		System.out.println("Number of bug 2: "+bug2);
		System.out.println("Number of bug 3: "+bug3);
		System.out.println("Number of bug 5: "+bug5);
		System.out.println("Number of bug 6: "+bug6);
		System.out.println("Number of bug 7: "+bug7);*/
	}

	
	private static void extractRandomsStats(String method){
		System.out.println("**************************************");
		System.out.println("one"+method+"Randoms:");
		System.out.println("Sample size: "+re.eval("nrow(one"+method+"Random0)").asInt());

		System.out.print("Failures: ");
		if(method.equals("Enabled")){
			re.assign("oneEnabledRandomFailures", ONE_ENABLED_RANDOMS_FAILURES);
			System.out.print(re.eval("mean(oneEnabledRandomFailures)").asDouble());
			System.out.println(" ("+re.eval("sd(oneEnabledRandomFailures)").asDouble()+")");
			System.out.println("Min: "+re.eval("min(oneEnabledRandomsFailures)").asInt());
			System.out.println("Max: "+re.eval("max(oneEnabledRandomsFailures)").asInt());
		} else{
			re.assign("oneDisabledRandomsFailures", ONE_DISABLED_RANDOMS_FAILURES);
			System.out.print(re.eval("mean(oneDisabledRandomsFailures)").asDouble());
			System.out.println(" ("+re.eval("sd(oneDisabledRandomsFailures)").asDouble()+")");
			System.out.println("Min: "+re.eval("min(oneEnabledRandomsFailures)").asInt());
			System.out.println("Max: "+re.eval("max(oneEnabledRandomsFailures)").asInt());
		}
		
		System.out.print("Faults: ");
		if(method.equals("Enabled")){
			re.assign("oneEnabledRandomsFaults", ONE_ENABLED_RANDOMS_FAULTS);
			System.out.print(re.eval("mean(oneEnabledRandomsFaults)").asDouble());
			System.out.println(" ("+re.eval("sd(oneEnabledRandomsFaults)").asDouble()+")");
			System.out.println("Min: "+re.eval("min(oneEnabledRandomsFaults)").asInt());
			System.out.println("Max: "+re.eval("max(oneEnabledRandomsFaults)").asInt());

		} else{
			re.assign("oneDisabledRandomsFaults", ONE_DISABLED_RANDOMS_FAULTS);
			System.out.print(re.eval("mean(oneDisabledRandomsFaults)").asDouble());
			System.out.println(" ("+re.eval("sd(oneDisabledRandomsFaults)").asDouble()+")");
			System.out.println("Min: "+re.eval("min(oneDisabledRandomsFaults)").asInt());
			System.out.println("Max: "+re.eval("max(oneDisabledRandomsFaults)").asInt());
		}
	}
	
	private static void extractFailures(Rengine re, String collection, int i, String method){
		if(method.startsWith("mostEnabledDisabled")||method.startsWith("randomMostEnabledDisabled")){
			MOST_ENABLED_DISABLED_RANDOMS_FAILURES[i] = re.eval("data.frame(table("+collection+"$Build))[1,2]").asInt();
		} else{
			if(method.equals("Enabled")){
				ONE_ENABLED_RANDOMS_FAILURES[i] = re.eval("data.frame(table(one"+method+"Random"+i+"$Build))[1,2]").asInt();
			} else{
				ONE_DISABLED_RANDOMS_FAILURES[i] = re.eval("data.frame(table(one"+method+"Random"+i+"$Build))[1,2]").asInt();
			}
		}
	}
}
