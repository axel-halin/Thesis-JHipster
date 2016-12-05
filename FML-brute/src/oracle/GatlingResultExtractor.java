package oracle;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Handles the extraction of Gatling tests results.
 * These results are then written in a CSV file
 * 
 * @author Alexandre Nuttinck
 */
public class GatlingResultExtractor {

	// Common test result files
	private static final String FILEJSON = "global_stats.json";


	private String path;
	private String buildTool;
	private String[] result = new String[42];

	public GatlingResultExtractor(String path, String buildTool){
		this.path = path;
		this.buildTool = buildTool;
		for(int i=0; i<result.length;i++) result[i] = "ND";
	}

	private static final Logger _log = Logger.getLogger("GatlingResultExtractor");

	/**
	 * Extract the result of Gatling tests. The result is the time needed to execute common (CRUD) operations.
	 * 
	 * @return result
	 */
	public String[] extractResultsGatlingTest(){

		String gatlingPath;
		if(buildTool.equals("maven")) 
		{
			String[] employeePATH = new File(path+"target/gatling/results/").list();	
			gatlingPath = "target/gatling/results/"+employeePATH[0]+"/js/";}
		else {
			String[] employeePATH = new File(path+"build/reports/gatling/").list();
			gatlingPath = "build/reports/gatling/"+employeePATH[0]+"/js/";
		}
		extractResultFromFile(path+gatlingPath+FILEJSON); 		// 42 results
		return result;
	}


	private void extractResultFromFile(String file){
		try{
			//Get Json strings used for the csv
			JsonParser jsonParser = new JsonParser();
			JsonObject objectGen = jsonParser.parse(Files.readFileIntoString(file)).getAsJsonObject();
			JsonObject numberOfRequests = (JsonObject) objectGen.get("numberOfRequests");
			JsonObject minResponseTime = (JsonObject) objectGen.get("minResponseTime");
			JsonObject maxResponseTime = (JsonObject) objectGen.get("maxResponseTime");
			JsonObject meanResponseTime = (JsonObject) objectGen.get("meanResponseTime");
			JsonObject standardDeviation = (JsonObject) objectGen.get("standardDeviation");
			JsonObject percentiles1 = (JsonObject) objectGen.get("percentiles1");
			JsonObject percentiles2 = (JsonObject) objectGen.get("percentiles2");
			JsonObject percentiles3 = (JsonObject) objectGen.get("percentiles3");
			JsonObject percentiles4 = (JsonObject) objectGen.get("percentiles4");
			JsonObject group1 = (JsonObject) objectGen.get("group1");
			JsonObject group2 = (JsonObject) objectGen.get("group2");
			JsonObject group3 = (JsonObject) objectGen.get("group3");
			JsonObject group4 = (JsonObject) objectGen.get("group4");
			JsonObject meanNumberOfRequestsPerSecond = (JsonObject) objectGen.get("meanNumberOfRequestsPerSecond");

			extractResultOneNodeTotal(numberOfRequests,0);
			extractResultOneNodeTotal(minResponseTime,3);
			extractResultOneNodeTotal(maxResponseTime,6);
			extractResultOneNodeTotal(meanResponseTime,9);
			extractResultOneNodeTotal(standardDeviation,12);
			extractResultOneNodeTotal(percentiles1,15);
			extractResultOneNodeTotal(percentiles2,18);
			extractResultOneNodeTotal(percentiles3,21);
			extractResultOneNodeTotal(percentiles4,24);
			extractResultOneNodeGroup(group1,27);
			extractResultOneNodeGroup(group2,30);
			extractResultOneNodeGroup(group3,33);
			extractResultOneNodeGroup(group4,36);
			extractResultOneNodeTotal(meanNumberOfRequestsPerSecond,39);
		} 
		catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}

	private void extractResultOneNodeTotal(JsonObject object, Integer index){

		try{
			result[index] = object.get("total").toString();
			result[index+1] =  object.get("ok").toString();
			result[index+2] =  object.get("ko").toString();
		}

		catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}

	private void extractResultOneNodeGroup(JsonObject object, Integer index){

		try{
			result[index] = object.get("name").toString();
			result[index+1] =  object.get("count").toString();
			result[index+2] =  object.get("percentage").toString();
		}

		catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}
}

