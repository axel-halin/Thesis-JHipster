package csv;

import java.io.IOException;
import java.util.List;

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
	   * create CSVfile 
	   * 
	   * @param filename Name of the CSVfile
	   * @param List of lines)
	   *  
	   */
	   public static void writeToCSVList(String filename, List<String[]> lines) throws IOException {
			CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
			String[] heads = {"JHipsterRegister", "Generate", "Build", "Log", "TimeBuild", "Memory", "Tests" };
			writer.writeNext(heads);
			writer.writeAll(lines);
			writer.close();
		}
}
