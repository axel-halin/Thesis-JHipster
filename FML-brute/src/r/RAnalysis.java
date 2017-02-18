package r;

import org.rosuda.JRI.Rengine;


public class RAnalysis {
	private static final String[] OPTIONALS = {"Docker","enableTranslation","enableSocialSignIn",
												"useSass", "websocket", "clusteredHttpSession",
												"searchEngine"};

	public static void main(String[] args) {
		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");

		Rengine re=new Rengine(args, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

		
		selectOneEnabled(re);
		selectOneDisabled(re);
		
		re.end();
		System.out.println("end");
	}
	
	/**
	 * Reads the CSV file csvfile in variable data.
	 * 
	 * @param re
	 * @param csvfile CSV file to read
	 * @param data Name of the dataframe in which to store the content
	 */
	public static void readCSV(Rengine re, String csvfile, String data){
		// Read CSV. and remove empty rows
		re.eval(""+data+"<-read.csv(file='"+ csvfile +"', na.strings = c(\"\", \"NA\"), head=TRUE, sep=',')");
	}
	
	
	/**
	 * Computes for each optional feature the proportion of failure and bugs with a one-enabled sample.
	 * 
	 * @param re
	 */
	private static void selectOneEnabled(Rengine re){
		readCSV(re,"jhipster.csv","data");
		for(String feat : OPTIONALS){
			selectEnabled(re,feat,feat+"OneEnabled","data");
			for (String feat2 : OPTIONALS){
				if (!feat2.equals(feat)){
					selectDisabled(re, feat2, feat+"OneEnabled", feat+"OneEnabled");
				}
			}

			// find proportion of failure
			extractFailureProportion(re,feat+"OneEnabled");
			// find proportion of bugs
			extractBugProportion(re,feat+"OneEnabled");
		}
	}
	
	
	private static void selectOneDisabled(Rengine re){
		readCSV(re,"jhipster.csv","data");
		for(String feat: OPTIONALS){
			selectDisabled(re,feat,feat+"OneDisabled","data");
			for (String feat2:OPTIONALS){
				if (!feat2.equals(feat)) selectEnabled(re,feat2,feat+"OneDisabled",feat+"OneDisabled");
			}
			
			extractFailureProportion(re, feat+"OneDisabled");
			extractBugProportion(re, feat+"OneDisabled");
		}
	}
	
	
	/**
	 * Select and store all rows of dataframe collection where feature is enabled in outputVar.
	 *
	 * @param re
	 * @param feature
	 * @param outputVar
	 * @param collection
	 */
	private static void selectEnabled(Rengine re, String feature,String outputVar, String collection){
		String value = "true|spring-websocket|hazelcast|elasticsearch|ehcache";
		
		String command = String.format("%s <- %s[grep(\"%s\",%s$%s),]",outputVar,collection,value,collection,feature);
		//_log.info(command);
		re.eval(command);
	}
	
	/**
	 * Select and store all rows of dataframe collection where feature is disabled in outputVar.
	 * 
	 * @param re
	 * @param feature
	 * @param outputVar
	 * @param collection
	 */
	private static void selectDisabled(Rengine re, String feature, String outputVar, String collection){
		String value = "no|false|ND";
		String command = String.format("%s <- %s[grep(\"%s\",%s$%s),]",outputVar,collection,value,collection,feature);
		//_log.info(command);
		re.eval(command);
	}
	
	/**
	 * Extract the failure proportion of dataframe collection.
	 * 
	 * A failure is a variant that cannot build (Build=KO).
	 * 
	 * @param re
	 * @param collection
	 */
	private static void extractFailureProportion(Rengine re, String collection){
		String buildOKCommand = String.format("nrow(%s[grep(\"OK\",%s$Build),])", collection,collection);
		String buildKOCommand = String.format("nrow(%s[grep(\"KO\",%s$Build),])", collection,collection);
		
		int buildOK = re.eval(buildOKCommand).asInt();
		int buildKO = re.eval(buildKOCommand).asInt();
		
		double total= (double) buildKO/(buildOK + buildKO);
		System.out.println("Proportion of failures in "+collection+" = "+total*100+"% ("+buildKO+"/"+(buildKO+buildOK)+") configs");
	}
	
	/**
	 * Extract the proportion of each bug in the dataframe collection.
	 * 
	 * More details about the bugs can be found in the reports.
	 * 
	 * @param re
	 * @param collection
	 */
	private static void extractBugProportion(Rengine re, String collection){
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
		
		System.out.println("Number of bug 1: "+bug1);
		System.out.println("Number of bug 2: "+bug2);
		System.out.println("Number of bug 3: "+bug3);
		System.out.println("Number of bug 5: "+bug5);
		System.out.println("Number of bug 6: "+bug6);
		System.out.println("Number of bug 7: "+bug7);
	}
}