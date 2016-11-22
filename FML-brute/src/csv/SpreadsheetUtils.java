package csv;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import com.google.api.services.sheets.v4.Sheets;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Class where we manage everything with the API Spreadsheet from Google
 *
 * @author Nuttinck Alexandre
 *
 */
public class SpreadsheetUtils{
	/** Application name. */
	private static final String APPLICATION_NAME =
			"Google Sheets API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	private static final List<String> SCOPES =
			Arrays.asList(SheetsScopes.SPREADSHEETS);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws Exception 
	 */
	public static Credential authorize() throws Exception {
		// Load client secrets.
		InputStream in =
				SpreadsheetUtils.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(
				flow, new LocalServerReceiver()).authorize("user");
		System.out.println(
				"Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * @return an authorized Sheets API client service
	 * @throws Exception 
	 */
	public static Sheets getSheetsService() throws Exception {
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
	/**
	 * Create Google Spreadsheet
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public static void createGoogleSpreadsheet(String filename) throws Exception{
		Sheets service = getSheetsService();
		
		SpreadsheetProperties properties = new SpreadsheetProperties()
				.setTitle(filename);
		Spreadsheet test = new Spreadsheet()
				.setProperties(properties)
				.setSpreadsheetId("jhipster");
		
		service.spreadsheets().create(test)
		.execute();
	}
	
	/**
	 * return the number of line if the configuration (yo-rc) doesn't already exist in the GoogleSpreadSheet
	 * 	else return -1
	 * @param id of the Google SpreadSheet
	 * @param line to check (yorc)
	 * @throws Exception 
	 *  
	 */
	public static Integer CheckNotExistLineSpreadSheet(String spreadsheetId, String[] line) throws Exception {   
		
		Sheets service = getSheetsService();
		
		String range = "A1:P";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		Integer check = values.size();
		if (values == null || values.size() == 0) {
			System.out.println("No data found.");
		} else {
			for (List row : values) {
				
				row = values;
				if (row.get(3).toString().equals(line[0].toString())&&row.get(4).toString().equals(line[1].toString())&&row.get(5).toString().equals(line[2].toString())
						&&row.get(6).toString().equals(line[3].toString())&&row.get(7).toString().equals(line[4].toString())&&
						row.get(8).toString().equals(line[5].toString())&&row.get(9).toString().equals(line[6].toString())
						&&row.get(10).toString().equals(line[7].toString())&&row.get(11).toString().equals(line[8].toString())
						&&row.get(12).toString().equals(line[9].toString())&&row.get(13).toString().equals(line[10].toString())
						&&row.get(14).toString().equals(line[11].toString())&&row.get(15).toString().equals(line[12].toString())&&row.get(16).toString().equals(line[13].toString()))
				{
					check = -1;
				};
			}
		}
		return check;
	}
	
	/**
	 * Add new line in the GoogleSpreadSheet at a specific line
	 * 	
	 * @param id of the Google SpreadSheet
	 * @param line to add
	 * @param number of the line
	 * @throws Exception 
	 *  
	 */
	public static void AddLineSpreadSheet(String spreadsheetId, String[] line, Integer numberOfLine) throws Exception {   
		
		Sheets service = getSheetsService();
		
		List<Request> requests = new ArrayList<>();
		
		List<CellData> values = new ArrayList<>();
		
		for (int i=0;i<line.length;i++)
		{
		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue(line[i].toString())
						));
		}

		requests.add(new Request()
				.setUpdateCells(new UpdateCellsRequest()
						.setStart(new GridCoordinate()
								.setSheetId(0)
								.setRowIndex(numberOfLine)
								.setColumnIndex(0))
						.setRows(Arrays.asList(
								new RowData().setValues(values)))
						.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
				.setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
		.execute();
	}


	/*@Test
	public void googleAPItestRead() throws Exception{
		// Build a new authorized API client service.
		Sheets service = getSheetsService();

		// Prints the names and majors of students in a sample spreadsheet:
		// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
		String range = "Class Data!A2:E";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("No data found.");
		} else {
			System.out.println("Name, Major");
			for (List row : values) {
				// Print columns A and E, which correspond to indices 0 and 4.
				System.out.printf("%s, %s\n", row.get(0), values.size());
			}
		}
	}*/

	/*@Test
	public void googleAPItestWrite() throws Exception{
		Sheets service = getSheetsService();
		String spreadsheetId = "1m-96Aeae_8Nb5lZAEnrlGhhoFcK13Tl646kfGO9q15Q";
		List<Request> requests = new ArrayList<>();

		List<CellData> values = new ArrayList<>();

		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue()
						.setStringValue("Hello World!")));
		requests.add(new Request()
				.setUpdateCells(new UpdateCellsRequest()
						.setStart(new GridCoordinate()
								.setSheetId(0)
								.setRowIndex(0)
								.setColumnIndex(0))
						.setRows(Arrays.asList(
								new RowData().setValues(values)))
						.setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
				.setRequests(requests);
		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
		.execute();
	}*/
	
	@Test
	public void googleAPItestCreate() throws Exception{
		//getGoogleSpreadsheet("1SQMaSYOKUIeRit8oHl3Hgo92G3LKFA9j7odPCmjs624");
	}

}