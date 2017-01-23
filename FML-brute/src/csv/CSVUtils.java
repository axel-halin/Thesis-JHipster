package csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Methods used for writing a Csv file
 * 
 * @author Nuttinck Alexandre
 * @author Axel Halin
 */

public class CSVUtils {
	private String path = null;
	private String JACOCOPATH = "/target/test-results/coverage/jacoco/";
	private static final Logger _log = Logger.getLogger("CSVUtils");

	public CSVUtils(String path){
		this.path = path;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){return path;}

	/**
	 * create CSVfile JHipster and heads of the file
	 * 
	 * @param filename Name of the CSVfile
	 * @param List of lines)
	 *  
	 */
	public static void createCSVFileJHipster(String filename) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
		String[] heads = {"Id","JHipsterRegister","Docker","applicationType","authenticationType","hibernateCache",
				"clusteredHttpSession","websocket","databaseType","devDatabaseType","prodDatabaseType",
				"buildTool","searchEngine","enableSocialSignIn","useSass","enableTranslation","testFrameworks","Generate",
				"Log-Gen","TimeToGenerate","Compile","Log-Compile","TimeToCompile","Build","Log-Build", "TimeToBuildDockerPackage", "TimeToBuild", 
				"ImageDocker","TestsResult", "Cucumber","KarmaJS","Gatling","Protractor","CoverageInstructions","CoverageBranches","JSStatementsCoverage","JSBranchesCoverage"};
		writer.writeNext(heads);
		writer.close();
	}

	/**
	 * create CSVfile Coverage and heads of the file
	 * 
	 * @param filename Name of the CSVfile
	 * @param List of lines)
	 *  
	 */
	public static void createCSVFileCoverage(String filename) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
		String[] heads = {"Id","JHipsterRegister","GROUP","PACKAGE","CLASS","INSTRUCTION_MISSED","INSTRUCTION_COVERED",
				"BRANCH_MISSED","BRANCH_COVERED","LINE_MISSED","LINE_COVERED","COMPLEXITY_MISSED",
				"COMPLEXITY_COVERED","METHOD_MISSED","METHOD_COVERED"};
		writer.writeNext(heads);
		writer.close();
	}

	/**
	 * Create Cucumber csv file if it doesn't already exist
	 * 
	 * @param fileName Name of the Cucumber csv file
	 */
	public static void createCSVCucumber(String fileName){
		File file = new File(fileName);
		if(!file.exists()) { 
			try{
				_log.info("Creating Cucumber csv file...");
				CSVWriter writer = new CSVWriter(new FileWriter(fileName), ';');
				String[] heads = {"Id","JHipsterRegister","isAuthenticated","getCurrentUserLogin","AnonymousIsNotAuthenticated","OnlyActivatedUserCanRequestPasswordReset",
						"UserMustExistToResetPassword","ResetKeyMustBeValid","UserCanResetPassword","FindNotActivatedUsersByCreationDateBefore",
						"RemoveOldPersistentTokens","ResetKeyMustNotBeOlderThan24Hours", "SaveInvalidLogin", "RegisterInvalidEmail", "RegisterInvalidLogin",
						"RegisterInvalidPassword", "RegisterAdminIsIgnored", "GetUnknownAccount", "RegisterDuplicateEmail", "RegisterDuplicateLogin",
						"AuthenticatedUser","testRegisterValid","testNonAuthenticatedUser","testGetExistingAccount","getNonExistingAudit", "getNonExistingAuditByDate",
						"getAllAudits", "getAuditsByDate","getAudit","testGetUnknownUser","testGetExistingUser",
						"getNonExistingCountry","createCountry","updateCountry","getAllCountries","deleteCountry","getCountry",
						"createDepartment","deleteDepartment","getAllDepartments","updateDepartment","getNonExistingDepartment","checkDepartmentNameIsRequired","getDepartment",
						"getEmployee","getNonExistingEmployee","deleteEmployee","getAllEmployees","updateEmployee","createEmployee",
						"updateJobHistory","getNonExistingJobHistory","getJobHistory","getAllJobHistories","createJobHistory","deleteJobHistory",
						"getNonExistingJob","updateJob","getJob","deleteJob","getAllJobs","createJob",
						"updateLocation","createLocation","getLocation","getNonExistingLocation","deleteLocation","getAllLocations",
						"getAllRegions","updateRegion","deleteRegion","getNonExistingRegion","getRegion","createRegion",
						"getAllTasks","updateTask","getTask","getNonExistingTask","createTask","deleteTask"
				};
				writer.writeNext(heads);
				writer.close();
			} catch (IOException e){
				_log.error("IOException: "+e.getMessage());
			}
		}
	}

	/**
	 * add new line to the CSVfile 
	 * 
	 * @param filename Name of the CSVfile
	 * @param new line)
	 *  
	 */
	public static void writeNewLineCSV(String filename, String[] line) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(filename, true),';');
		writer.writeNext(line);
		writer.close();
	}

	/**
	 * add new line to the CoverageCSV
	 * 
	 * @param filename Name of the CSVfile
	 * @param new line)
	 *  
	 */
	public void writeLinesCoverageCSV(String filename,String filename2,String jDirectory,String Id) throws IOException {  

		try{
			CSVReader lines = new CSVReader(new FileReader(path+JACOCOPATH+filename), ',');
			String[] row = null;

			List content = lines.readAll();

			for (Object object : content) {

				row = (String[]) object;

				String[] newline = {Id,jDirectory,row[0],row[1],row[2],row[3],row[4],row[5],row[6],row[7],row[8],
						row[9],row[10],row[11],row[12]};

				{
					CSVWriter writer = new CSVWriter(new FileWriter(filename2, true),';');
					writer.writeNext(newline);
					writer.close();
				};

			}
			lines.close();
		} 
		catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}

	/**
	 * return true if the configuration (yo-rc) doesn't already exist in the CSVfile
	 * 
	 * @param filename Name of the CSVfile
	 * @param new line)
	 *  
	 */
	public static boolean CheckNotExistLineCSV(String filename, String[] line) throws IOException {  

		boolean check = true;
		CSVReader lines = new CSVReader(new FileReader(filename), ';');
		String[] row = null;

		List content = lines.readAll();

		for (Object object : content) {

			row = (String[]) object;

			// Change if we test more variants...

			if (row[3].toString().equals(line[0].toString())&&row[4].toString().equals(line[1].toString())&&row[5].toString().equals(line[2].toString())
					&&row[6].toString().equals(line[3].toString())&&row[7].toString().equals(line[4].toString())&&
					row[8].toString().equals(line[5].toString())&&row[9].toString().equals(line[6].toString())
					&&row[10].toString().equals(line[7].toString())&&row[11].toString().equals(line[8].toString())
					&&row[12].toString().equals(line[9].toString())&&row[13].toString().equals(line[10].toString())
					&&row[14].toString().equals(line[11].toString())&&row[15].toString().equals(line[12].toString())&&row[16].toString().equals(line[13].toString()))
			{
				check = false;
			};
		}
		lines.close();
		return check;
	}

	/**
	 * Extract bugs from CSV jhipster into a new shorter CSV
	 * 
	 * @param filename Name of the CSVfile
	 * @param filename2 Name of the CSVfile
	 * @param doublon Check doublon if true
	 *  
	 */
	public static void createBugsCSV(String filename,String filename2,boolean doublon) throws IOException {  
		CSVWriter writerInit = new CSVWriter(new FileWriter(filename2), ';');
		String[] heads = {"Id","Log-Build"};
		writerInit.writeNext(heads);
		writerInit.close();

		CSVReader lines = new CSVReader(new FileReader(filename), ';');
		String[] row = null;

		List content = lines.readAll();

		for (Object object : content) {

			row = (String[]) object;

			String[] newline = {row[0],row[23]};

			if (row[22].equals("KO"))
			{

				boolean check = true;
				CSVReader lines2 = new CSVReader(new FileReader(filename2), ';');
				String[] row2 = null;

				List content2 = lines2.readAll();

				for (Object object2 : content2) {

					row2 = (String[]) object2;

					if (row[23].toString().equals(row2[1].toString()) && doublon)
					{
						// false if bugs already appears
						check = false;

						System.out.println("equals");
					};
				}
				lines2.close();

				if (check)
				{
					CSVWriter writer = new CSVWriter(new FileWriter(filename2, true),';');
					writer.writeNext(newline);
					writer.close();
				}
			};

		}
		lines.close();
	} 
	
	/**
	 * Add new column in jhipster.csv bugs to categorize bugs.
	 * 
	 * @param filename Name of the CSVfile
	 * @param filename2 Name of the CSVfile
	 * @param doublon Check doublon if true
	 *  
	 */
	public static void categorizeBugsCSV(String filename, String filename2) throws IOException {  
		CSVReader lines = new CSVReader(new FileReader(filename), ',');
		String[] row = null;

		List content = lines.readAll();

		for (Object object : content) {

			row = (String[]) object;
			String bug = "newBUG";
						
			if (row[23].equals("KO")) // build = KO
			{
				//extract log
				String logCompilation = row[21];
				String logBuild = row[24];

				//CHECK LOG TO CATEGORIZE BUGS Error parsing reference: Could not connect to address=(host=mariadb)(port=3306)(type=master) : Connection refused 
				Matcher m0 = Pattern.compile("(.*?)SocialUserConnection").matcher(logCompilation);
				Matcher m1 = Pattern.compile("(.*?)Failed to get driver instance for jdbcUrl=jdbc:mariadb://(.*?):3306/jhipster").matcher(logBuild);
				Matcher m3 = Pattern.compile("(.*?)No instances available for uaa").matcher(logBuild);
				Matcher m4 = Pattern.compile("(.*?)\"jhipster - jhipster-mariadb(.*?)is not a valid repository/tag").matcher(logBuild);
				Matcher m5 = Pattern.compile("(.*?)com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").matcher(logBuild);
				Matcher m6 = Pattern.compile("(.*?)org.springframework.security.oauth2.provider.token.store.JdbcTokenStore").matcher(logBuild);

				while(m0.find()) bug = "BUG6:SOCIALLOGIN";
				
				while(m1.find()) bug = "BUG1:mariadb";
				
				while(m3.find()) bug = "BUG2:UAAAuthentication";
				
				while(m4.find()) bug = "BUG3:mariadb";
				
				while(m5.find()) bug = "BUG4:SQL";
				
				while(m6.find()) bug = "BUG5:OAUTH2";
				
				String[] newline = {row[0],row[1],row[2],row[3],row[4],row[5],row[6],row[7],row[8],
						row[9],row[10],row[11],row[12],row[13],row[14],row[15],
						row[16],row[17],row[18],row[19],row[20],row[21],row[22],row[23],row[24],bug};
				
				CSVWriter writer = new CSVWriter(new FileWriter(filename2, true),',');
				writer.writeNext(newline);
				writer.close();
				}
			};
			lines.close();
}


	/**
	 * Create new CSV with configurations from CSV t-wise from CSV
	 * 
	 * @param filename Name of the complete CSVfile
	 * @param filename2 Name of the twise CSVfile
	 * @param outputCSV Name of the output CSVfile
	 *  
	 */
	public static void createNwiseCSV(String completeCSV,String twiseCSV, String outputCSV) throws IOException {  
		CSVReader lines = new CSVReader(new FileReader(completeCSV), ',');
		String[] row = null;

		int i = 0;

		List content = lines.readAll();
		content.remove(0);

		for (Object object : content) {

			row = (String[]) object;
			String[] line = {row[0],row[1],row[2],row[3],row[4],row[5],row[6],row[7],row[8],
					row[9],row[10],row[11],row[12],row[13],row[14],row[15],
					row[16],row[17],row[18],row[19],row[20],row[21],row[22],row[23],row[24],
					row[25],row[26],row[27],row[28],row[29],row[30],row[31],row[32],
					row[33],row[34],row[35]};

			CSVReader lines2 = new CSVReader(new FileReader(twiseCSV), ';');
			String[] twise = null;

			List content2 = lines2.readAll();
			content2.remove(0);

			for (Object object2 : content2) {

				boolean exist = true;
				twise = (String[]) object2;

				//----------------------------------DOCKER
				//true
				if ((row[2].toString().equals("true"))&&(twise[8].toString().equals("-")))
				{exist = false;}
				//false 
				if ((row[2].toString().equals("false"))&&(twise[8].toString().equals("X")))
				{exist = false;}

				//----------------------------------APPLICATION TYPE
				//applicationType monolith
				if ((row[3].toString().equals("\"monolith\""))&&(twise[27].toString().equals("-")))
				{exist = false;}
				//applicationType gateway 
				if ((row[3].toString().equals("\"gateway\""))&&(twise[17].toString().equals("-")))
				{exist = false;}
				//applicationType uaa
				if ((row[3].toString().equals("\"uaa\""))&&(twise[33].toString().equals("-")))
				{exist = false;}
				//applicationType microservice
				if ((row[3].toString().equals("\"microservice\""))&&(twise[32].toString().equals("-")))
				{exist = false;}

				//----------------------------------AUTHENTICATION TYPE		
				//authenticationType session
				if ((row[4].toString().equals("\"session\""))&&(twise[21].toString().equals("-")))
				{exist = false;}
				//applicationType uaa
				if ((row[4].toString().equals("\"uaa\""))&&(twise[30].toString().equals("-")))
				{exist = false;}
				//applicationType oauth2
				if ((row[4].toString().equals("\"oauth2\""))&&(twise[16].toString().equals("-")))
				{exist = false;}
				//applicationType jwt
				if ((row[4].toString().equals("\"jwt\""))&&(twise[18].toString().equals("-")))
				{exist = false;}
				//----------------------------------HIBERNATE CACHE		
				//hazelcast
				if ((row[5].toString().equals("\"hazelcast\""))&&(twise[29].toString().equals("-")))
				{exist = false;}
				//ehcache
				if ((row[5].toString().equals("\"ehcache\""))&&(twise[15].toString().equals("-")))
				{exist = false;}
				if ((row[5].toString().equals("\"no\""))&&((twise[15].toString().equals("X"))||(twise[29].toString().equals("X"))))
				{exist = false;}
				//----------------------------------CLUSTERED HTTPSESSION		
				//clustered http session
				if ((row[6].toString().equals("\"hazelcast\""))&&(twise[43].toString().equals("-")))
				{exist = false;}
				if (((row[6].toString().equals("\"no\""))||(row[6].toString().equals("ND")))&&(twise[43].toString().equals("X")))
				{exist = false;}
				//----------------------------------WEBSOCKET		
				//spring-websocket
				if ((row[7].toString().equals("\"spring-websocket\""))&&(twise[35].toString().equals("-")))
				{exist = false;}
				if (((row[7].toString().equals("\"no\""))||(row[7].toString().equals("ND")))&&(twise[35].toString().equals("X")))
				{exist = false;}
				//----------------------------------DATABASE TYPE
				//databaseType SQL
				if ((row[8].toString().equals("\"sql\""))&&(twise[26].toString().equals("-")))
				{exist = false;}
				//databaseType MongoDB 
				if ((row[8].toString().equals("\"mongodb\""))&&(twise[36].toString().equals("-")))
				{exist = false;}
				//databaseType Cassandra
				if ((row[8].toString().equals("\"cassandra\""))&&(twise[5].toString().equals("-")))
				{exist = false;}
				if ((row[8].toString().equals("\"no\""))&&(twise[13].toString().equals("X")))
				{exist = false;}
				//----------------------------------DEVDATABASE TYPE
				//databaseType mysql
				if ((row[9].toString().equals("\"mysql\""))&&(twise[11].toString().equals("-")))
				{exist = false;}
				//databaseType MariaDB 
				if ((row[9].toString().equals("\"mariadb\""))&&(twise[42].toString().equals("-")))
				{exist = false;}
				//databaseType postgresql
				if ((row[9].toString().equals("\"postgresql\""))&&(twise[38].toString().equals("-")))
				{exist = false;}		
				//databaseType DiskBased
				if ((row[9].toString().equals("\"DiskBased\""))&&(twise[25].toString().equals("-")))
				{exist = false;}	
				//databaseType InMemory
				if ((row[9].toString().equals("\"InMemory\""))&&(twise[24].toString().equals("-")))
				{exist = false;}

				//----------------------------------PROD DATABASE TYPE
				//databaseType mysql
				if ((row[10].toString().equals("\"mysql\""))&&(twise[12].toString().equals("-")))
				{exist = false;}
				//databaseType MariaDB 
				if ((row[10].toString().equals("\"mariadb\""))&&(twise[22].toString().equals("-")))
				{exist = false;}
				//databaseType postgresql
				if ((row[10].toString().equals("\"postgresql\""))&&(twise[4].toString().equals("-")))
				{exist = false;}		

				//----------------------------------BUILD TOOL
				//maven
				if ((row[11].toString().equals("\"maven\""))&&(twise[0].toString().equals("-")))
				{exist = false;}
				//gradle 
				if ((row[11].toString().equals("\"gradle\""))&&(twise[39].toString().equals("-")))
				{exist = false;}
				//----------------------------------SEARCH ENGINE	
				//elasticsearch
				if ((row[12].toString().equals("\"elasticsearch\""))&&(twise[7].toString().equals("-")))
				{exist = false;}
				if ((row[12].toString().equals("\"no\""))&&(twise[7].toString().equals("X")))
				{exist = false;}
				//----------------------------------SOCIAL LOGIN
				//social login
				if ((row[13].toString().equals("true"))&&(twise[20].toString().equals("-")))
				{exist = false;}
				if ((row[13].toString().equals("false"))&&(twise[20].toString().equals("X")))
				{exist = false;}
				if ((row[13].toString().equals("ND"))&&(twise[20].toString().equals("X")))
				{exist = false;}
				//----------------------------------USESASS
				//usesass
				if ((row[14].toString().equals("true"))&&(twise[28].toString().equals("-")))
				{exist = false;}
				if ((row[14].toString().equals("false"))&&(twise[28].toString().equals("X")))
				{exist = false;}
				if ((row[14].toString().equals("ND"))&&(twise[28].toString().equals("X")))
				{exist = false;}
				//----------------------------------TRANSLATION
				//translation
				if ((row[15].toString().equals("true"))&&(twise[19].toString().equals("-")))
				{exist = false;}
				if ((row[15].toString().equals("false"))&&(twise[19].toString().equals("X")))
				{exist = false;}
				if ((row[15].toString().equals("ND"))&&(twise[19].toString().equals("X")))
				{exist = false;}
				//----------------------------------TEST FRAMEWORKS
				//without protractor
				if ((row[16].toString().equals("[\"gatling\",\"cucumber\"]"))&&(twise[37].toString().equals("X")))
				{exist = false;}
				if ((row[16].toString().equals("[\"cucumber\",\"gatling\"]"))&&(twise[37].toString().equals("X")))
				{exist = false;}

				if (exist)
				{
					System.out.println(row[0]+row[1]+row[2]+row[3]+row[4]+row[5]+row[13]);
					//System.out.println(row[1]);
					i++;
					CSVWriter writer = new CSVWriter(new FileWriter(outputCSV, true),';');
					writer.writeNext(line);
					writer.close();
				}
			}
			lines2.close();
		}
		lines.close();
		System.out.println("Total: "+i);
	}
	
	
	public static void extractRandomConfigs(String inputFile, String outputFile, int nConfigs, int nIterations) throws IOException{
		CSVReader lines = new CSVReader(new FileReader(inputFile), ',');
		List<String[]> content = lines.readAll();
		
		for (int i=0; i<nIterations;i++){
			System.out.println(content.isEmpty());
			// Shuffle list
			Collections.shuffle(content);
			int randomNum = ThreadLocalRandom.current().nextInt(1, content.size() - nConfigs);
			// Write lines
			CSVWriter writer = new CSVWriter(new FileWriter(outputFile+(i+1)+".csv", true),';');
			for(int j=randomNum;j<=randomNum+nConfigs-1;j++){
				writer.writeNext(content.get(j));
			}
			writer.close();
		}
		
		lines.close();
	}
} 