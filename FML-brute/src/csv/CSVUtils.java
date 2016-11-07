package csv;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
				"searchEngine","enableSocialSignIn","useSass","enableTranslation","testFrameworks","Generate",
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
	 * return true if the configuration (yo-rc) doesn't already exist in the CSVfile
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

			if (row[3].toString().equals(line[0].toString())&&row[4].toString().equals(line[1].toString())&&row[5].toString().equals(line[2].toString())
					&&row[6].toString().equals(line[3].toString())&&row[7].toString().equals(line[4].toString())&&
					row[8].toString().equals(line[5].toString())&&row[9].toString().equals(line[6].toString())
					&&row[10].toString().equals(line[7].toString())&&row[11].toString().equals(line[8].toString())
					&&row[12].toString().equals(line[9].toString())&&row[13].toString().equals(line[10].toString())
					&&row[14].toString().equals(line[11].toString())&&row[15].toString().equals(line[12].toString()))
			{
				check = false;
			};
		}
		lines.close();
		return check;
	}
}