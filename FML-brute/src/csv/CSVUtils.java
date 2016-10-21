package csv;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileReader;
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

			if (row[2].toString().equals(line[0].toString())&&row[3].toString().equals(line[1].toString())&&row[4].toString().equals(line[2].toString())
					&&row[5].toString().equals(line[3].toString())&&row[6].toString().equals(line[4].toString())&&
					row[7].toString().equals(line[5].toString())&&row[8].toString().equals(line[6].toString())
					&&row[9].toString().equals(line[7].toString())&&row[10].toString().equals(line[8].toString())
					&&row[11].toString().equals(line[9].toString())&&row[12].toString().equals(line[10].toString())
					&&row[13].toString().equals(line[11].toString())&&row[14].toString().equals(line[12].toString()))
			{
				check = false;
			};

		}
		lines.close();
		return check;
	}
}