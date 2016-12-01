/**
 * Represents a specific configuration of JHipster.
 * Each attribute stands for a field in the yo-rc.json that will be generated.
 * 
 * edited by Axel on September 20th, 2016.
 */
public class JhipsterConfiguration {
		
	String baseName;
	String packageName;
	String packageFolder;
	String serverPort;
	String authenticationType;
	String hibernateCache;
	String clusteredHttpSession;
	String websocket;
	String databaseType;
	String devDatabaseType;
	String prodDatabaseType;
	String searchEngine;
	String buildTool;
	String jwtSecretKey;
	protected String applicationType; // gateway | microservice | monolith | uaa
	String[] testFrameworks;
	String jhiPrefix;
	String jhipsterVersion;
		
	Boolean useSass = null;
	boolean enableTranslation; 
	
	String nativeLanguage;
	//String[] languages = new String[2];
	Boolean skipClient;
	Boolean skipUserManagement;
	Boolean enableSocialSignIn;
	String rememberMeKey;
	String uaaBaseName;
		
		
		// TODO update toString and equals.
		
		@Override
		public String toString() {
			String r = baseName + " " + packageName + " " + packageFolder + " " + authenticationType + " " + applicationType
					+ " " + hibernateCache + " " + clusteredHttpSession + " " + websocket + 
					" " + databaseType + " " + devDatabaseType + " " + prodDatabaseType + 
					" " + searchEngine + " " + /*useSass + */" " + buildTool + " " + " " /*+ enableTranslation + " " + enableSocialSignIn */
					;
			
			String strTestFrameworks = "[";
			if(testFrameworks!=null){
			for (int i = 0; i < testFrameworks.length; i++) {
				strTestFrameworks += testFrameworks[i];
				if (i < (testFrameworks.length - 1))
					strTestFrameworks += ",";
			}
			strTestFrameworks += "]";
			}
			return r + " " + strTestFrameworks;
		}
		
		
		@Override
		public boolean equals(Object o) {
			if ( ! (o instanceof JhipsterConfiguration))
				return false;
			
			JhipsterConfiguration jo = (JhipsterConfiguration) o;
			
			if (testFrameworks == null && jo.testFrameworks != null)
				 return false;
			if (testFrameworks != null && jo.testFrameworks == null)
				 return false;

			if (testFrameworks == null && jo.testFrameworks == null) {
				
			} // != null && != null
			else { 
				if (testFrameworks.length != jo.testFrameworks.length)
					return false;
				
				for (String t1 : testFrameworks) {
					boolean found = false; 
					for (String t2 : jo.testFrameworks) {
						if (t1.equals(t2))
							found = true;
					}
					if (!found)
						return false;
					// otherwiste continue
				}
			}
					
			return jo.baseName.equals(baseName)
					&& jo.packageName.equals(packageName)
					&& jo.packageFolder.equals(packageFolder)
					&& jo.authenticationType.equals(authenticationType)
					&& jo.hibernateCache.equals(hibernateCache)
					&& jo.clusteredHttpSession.equals(clusteredHttpSession)
					&& jo.websocket.equals(websocket)
					&& jo.databaseType.equals(databaseType)
					&& jo.devDatabaseType.equals(devDatabaseType)
					&& jo.prodDatabaseType.equals(prodDatabaseType)
					&& jo.searchEngine == searchEngine
					//&& jo.useSass == useSass
					&& jo.buildTool.equals(buildTool)
					//&& jo.enableTranslation == enableTranslation
					//&& jo.enableSocialSignIn == enableSocialSignIn
					&& jo.applicationType == applicationType
					// TODO
					;
					
			
		}
		
		
		

}
