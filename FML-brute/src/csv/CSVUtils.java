package csv;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;

/**
 * Methods used for writing a Csv file
 * 
 * @author Nuttinck Alexandre
 *
 */

public class CSVUtils {

	  /**
	   * create CSVfile and heads of the file
	   * 
	   * @param filename Name of the CSVfile
	   * @param List of lines)
	   *  
	   */
	   public static void createCSVFile(String filename) throws IOException {
			CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
			String[] heads = {"JHipsterRegister","Docker","applicationType","authenticationType","hibernateCache",
					"clusteredHttpSession","websocket","databaseType","devDatabaseType","prodDatabaseType",
					"searchEngine","enableSocialSignIn","useSass","enableTranslation","testFrameworks","Generate",
					"Log-Gen","TimeToGenerate(secs)","Compile","Log-Compile","TimeToCompile(secs)","Build","Log-Build", "TimeToBuild(secs)", "Memory", 
					"TestsResult", "Cucumber","KarmaJS","Gatling","Protractor" };
			writer.writeNext(heads);
			writer.close();
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
}
