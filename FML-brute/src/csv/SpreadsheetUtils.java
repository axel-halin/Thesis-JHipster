package csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Class where we manage everything with the API Spreadsheet from Google
 *
 * @author Nuttinck Alexandre
 *
 */
public class SpreadsheetUtils{
	private String path = null;
	private String JACOCOPATH = "/target/test-results/coverage/jacoco/";
	private static final Logger _log = Logger.getLogger("SpreadsheetUtils");

	public SpreadsheetUtils(String path){
		this.path = path;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){return path;}
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
	
	private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
	    return new HttpRequestInitializer() {
	        @Override
	        public void initialize(HttpRequest httpRequest) throws IOException {
	            requestInitializer.initialize(httpRequest);
	            httpRequest.setConnectTimeout(10 * 60000);  // 3 minutes connect timeout
	            httpRequest.setReadTimeout(10 * 60000);  // 3 minutes read timeout
	        }
	    };
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
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
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

		String range = "A1:Q";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		Integer check = 0;
		if (values == null || values.size() == 0) {
			System.out.println("No data found.");
		} else {
			check = values.size();
			System.out.println(check);
			for (List row : values) {

				if (!row.isEmpty())
				{	
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

	/**
	 * add new line to the Coverage Spreadsheet
	 *  
	 * @param id of the Google SpreadSheet
	 * @param line to add
	 * @param number of the line
	 * @param filename Name of the CSVfile
	 * 
	 * @throws Exception 
	 *  
	 */
	public void writeLinesCoverageCSV(String filename,String spreadsheetId,String jDirectory,String Id, Integer numberOfLine) throws Exception {  

		try{
			CSVReader lines = new CSVReader(new FileReader(path+JACOCOPATH+filename), ',');
			String[] row = null;
			ArrayList<String> array = new ArrayList<String>();
			array.add(0, "");
			array.add(1, "");
			array.add(2, "");
			array.add(3, "");
			array.add(4, "");
			array.add(5, "");
			array.add(6, "");
			array.add(7, "");
			array.add(8, "");
			array.add(9, "");
			array.add(10, "");
			array.add(11, "");
			array.add(12, "");
			array.add(13, "");
			array.add(14, "");

			List content = lines.readAll();

			for (Object object : content) {

				row = (String[]) object;

				array.set(0, Id);
				array.set(1, jDirectory);
				array.set(2, array.get(2) + row[0]+";");
				array.set(3, array.get(3) + row[1]+";");
				array.set(4, array.get(4) + row[2]+";");
				array.set(5, array.get(5) + row[3]+";");
				array.set(6, array.get(6) + row[4]+";");
				array.set(7, array.get(7) + row[5]+";");
				array.set(8, array.get(8) + row[6]+";");
				array.set(9, array.get(9) + row[7]+";");
				array.set(10, array.get(10) + row[8]+";");
				array.set(11, array.get(11) + row[9]+";");
				array.set(12, array.get(12) + row[10]+";");
				array.set(13, array.get(13) + row[11]+";");
				array.set(14, array.get(14) + row[12]+";");

			}
			lines.close();
			{
				Sheets service = getSheetsService();

				List<Request> requests = new ArrayList<>();

				List<CellData> values = new ArrayList<>();

				for (int i=0;i<array.size();i++)
				{
					values.add(new CellData()
							.setUserEnteredValue(new ExtendedValue()
									.setStringValue(array.get(i).toString())
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
			};


		} 
		catch (Exception e){
			
			_log.error("Exception: "+e.getMessage());

			//add ND if there is an Exception
			ArrayList<String> array = new ArrayList<String>();
			array.add(0, Id);
			array.add(1, jDirectory);
			array.add(2, "ND");
			array.add(3, "ND");
			array.add(4, "ND");
			array.add(5, "ND");
			array.add(6, "ND");
			array.add(7, "ND");
			array.add(8, "ND");
			array.add(9, "ND");
			array.add(10, "ND");
			array.add(11, "ND");
			array.add(12, "ND");
			array.add(13, "ND");
			array.add(14, "ND");

			Sheets service = getSheetsService();

			List<Request> requests = new ArrayList<>();

			List<CellData> values = new ArrayList<>();

			for (int i=0;i<array.size();i++)
			{
				values.add(new CellData()
						.setUserEnteredValue(new ExtendedValue()
								.setStringValue(array.get(i).toString())
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
		};

	}


/**
 * return the number of line of a GoogleSpreadSheet
 * 
 * @param id of the Google SpreadSheet
 * @throws Exception 
 *  
 */
public static Integer CheckNumberLineSpreadSheet(String spreadsheetId) throws Exception {   

	Sheets service = getSheetsService();
	
	String range = "A1";
	ValueRange response = service
			.spreadsheets().values()
			.get(spreadsheetId, range)
			.execute();
	List<List<Object>> values = response
			.getValues();
	Integer check = 0;
	if (values != null && values.size() != 0) {
		check = values.size();
	}
	return check;
}
}