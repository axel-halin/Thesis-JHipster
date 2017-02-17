package r;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;
import org.rosuda.JRI.Rengine;


public class RAnalysis {
	private static final Logger _log = Logger.getLogger("RAnalysis");
	
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

		
		// TODO
		selectOneEnabled(re);
		
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
		//readCSV(re,"jhipster.csv","data");
		readCSV(re,"jhipster.csv","data");
		for(String feat : OPTIONALS){
			selectEnabled(re,feat,feat+"OneEnabled","data");
			for (String feat2 : OPTIONALS){
				if (!feat2.equals(feat)){
					selectDisabled(re, feat2, feat+"OneEnabled", feat+"OneEnabled");
				}
			}

			// TODO 
			// find proportion of failure
			extractFailureProportion(re,feat+"OneEnabled");
			// find proportion of bugs
			extractBugProportion(re,feat+"OneEnabled");
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
	
	private static void extractFailureProportion(Rengine re, String collection){
		String buildOKCommand = String.format("nrow(%s[grep(\"OK\",%s$Build),])", collection,collection);
		String buildKOCommand = String.format("nrow(%s[grep(\"KO\",%s$Build),])", collection,collection);
		
		int buildOK = re.eval(buildOKCommand).asInt();
		int buildKO = re.eval(buildKOCommand).asInt();
		
		double total= (double) buildKO/(buildOK + buildKO);
		System.out.println("Proportion of failures in "+collection+" = "+total*100+"% ("+buildKO+"/"+(buildKO+buildOK)+") configs");
	}
	
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
		//TODO split bug2 according to Docker or Maven/Gradle
		// Bug 2: uaa
		String bug2Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex2,collection);
		// Bug 3: mariadb Docker
		String bug3Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex3,collection);
		// Bug 5: oauth2
		String bug5Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Build),])", collection,regex5,collection);;
		// Bug 6: social login
		String bug6Command = String.format("nrow(%s[grep(\"%s\",%s$Log.Compile),])",collection,regex6,collection);
		
		System.out.println(re.eval(bug1Command));
		System.out.println(re.eval(bug2Command));
		System.out.println(re.eval(bug3Command));
		System.out.println(re.eval(bug6Command));
		System.out.println(re.eval(bug5Command));
	}
}
