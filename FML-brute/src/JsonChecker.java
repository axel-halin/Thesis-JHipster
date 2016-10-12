import java.io.File;

import org.eclipse.xtext.util.Files;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Object to retrieve the different yo-rc.json templates from the folder.
 * 
 * These templates are then stored in a JsonObject array.
 * 
 * The location (yo-rc-templates/) and naming convention (beginning of the file's name) must be respected.
 * 
 * @author Axel
 */
// TODO Not optimal: re-scan the list of files everytime ==> refactor one scan add to the corresponding array on the fly.
public class JsonChecker {
	
	// TODO dynamically retrieve number of json per app type (list all files and increment counters)
	
	private static final String YO_RC_DIRECTORY = "yo-rc-templates/";
	private static JsonObject[] clientAppJson = null;
	private static JsonObject[] serverAppJson = null;
	private static JsonObject[] uaa = null;
	private static JsonObject[] monolith = null;
	private static JsonObject[] gateway = null;
	private static JsonObject[] microservice = null;
	
	/**
	 * Returns all yo-rc.json templates related to the clientApp.
	 * All these templates must be located in: yo-rc-templates/ and start with clientApp
	 * 
	 * @return yo-rc.json Templates as JsonObject
	 */
	public JsonObject[] getClientAppJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if (clientAppJson == null){
			clientAppJson = new JsonObject[2];
			for(final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if(file.getName().startsWith("clientApp")){ 
					JsonObject object = jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					clientAppJson[i] = object;
					i++;
				}
			}
		}
		return clientAppJson;
	}
	
	/**
	 * Returns all yo-rc.json templates related to the serverApp.
	 * All these templates must be located in: yo-rc-templates/ and start with serverApp 
	 * 
	 * @return
	 */
	public JsonObject[] getServerAppJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if(serverAppJson == null){
			serverAppJson = new JsonObject[4];
			for(final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if(file.getName().startsWith("serverApp")){
					JsonObject object= jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					serverAppJson[i] = object;
					i++;
				}
			}
		}
		return serverAppJson;
	}

	public JsonObject[] getUaaJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if (uaa == null){
			uaa = new JsonObject[2];
			for (final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if(file.getName().startsWith("uaa")){
					JsonObject object = jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					uaa[i] = object;
					i++;
				}
			}
		}
		return uaa;
	}
	
	public JsonObject[] getMonolithJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if(monolith == null){
			monolith = new JsonObject[4];
			for(final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if (file.getName().startsWith("monolith")){
					JsonObject object = jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					monolith[i] = object;
					i++;
				}
			}
		}
		return monolith;
	}
	
	public JsonObject[] getGatewayJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if(gateway == null){
			gateway = new JsonObject[4];
			for(final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if(file.getName().startsWith("gateway")){
					JsonObject object = jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					gateway[i] = object;
					i++;
				}
			}
		}
		return gateway;
	}
	
	public JsonObject[] getMicroserviceJson(){
		int i = 0;
		JsonParser jsonParser = new JsonParser();
		if (microservice == null){
			microservice = new JsonObject[4];
			for(final File file:new File(YO_RC_DIRECTORY).listFiles()){
				if (file.getName().startsWith("microservice")){
					JsonObject object = jsonParser.parse(Files.readFileIntoString(YO_RC_DIRECTORY+file.getName())).getAsJsonObject();
					microservice[i] = object;
					i++;
				}
			}
		}
		return microservice;
	}
}
