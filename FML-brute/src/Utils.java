import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/*
 * Templates can be retrieved from 	generator-jhipster-xxx/test/templates where xxx is the version number
 */
//TODO also check values of the keys....
public class Utils {


	public static boolean testJson(JsonElement json, JsonChecker jsonChecker){
		JsonObject object = json.getAsJsonObject().get("generator-jhipster").getAsJsonObject();
		if(object.has("applicationType")){
			switch(object.get("applicationType").getAsString()){
				case "monolith": 	return monolithCheck(object, jsonChecker);
				case "microservice": return microserviceCheck(object, jsonChecker);
				case "gateway": return gatewayCheck(object, jsonChecker);
				case "uaa": return uaaCheck(object, jsonChecker);
			}
			return false;
		} 
		else return appCheck(object, jsonChecker);
	}
	
	
	private static boolean uaaCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size == 17 || size==19){
			JsonObject[] templates = jsonChecker.getUaaJson();
			for(JsonObject obj:templates) if(haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	private static boolean gatewayCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size == 20 || size == 22){
			JsonObject[] templates = jsonChecker.getGatewayJson();
			for(JsonObject obj:templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	private static boolean microserviceCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size >=19 && size<=22){
			JsonObject[] templates = jsonChecker.getMicroserviceJson();
			for (JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	
	private static boolean monolithCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if(size == 19 || size == 21 || size == 22){
			JsonObject[] templates = jsonChecker.getMonolithJson();
			for (JsonObject obj:templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	/**
	 * Checks the validity of the ClientApp and ServerApp yo-rc.json against JHipster templates
	 * 
	 * @param object JsonObject to be tested
	 * @param jsonChecker JsonChecker which holds the templates
	 * @return True if object JSON is valid; False otherwise
	 */
	private static boolean appCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();

		// Client
		if(size == 2 || size == 4){
			JsonObject[] templates = jsonChecker.getClientAppJson();
			for(JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		// Server
		else if (size==15 || size==17 || size==19) {
			JsonObject[] templates = jsonChecker.getServerAppJson();
			for (JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		// Error
		else return false;
	}
	
	
	/*
	 * Checks if 2 JsonObject have the same skeleton (all the same attributes, no matter their values).
	 */
	private static boolean haveSameSkeleton(JsonObject object1, JsonObject object2){
		boolean res = (object1.entrySet().size() == object2.entrySet().size());
		System.err.println(res);
		if (res){
			for(java.util.Map.Entry<String, JsonElement> entry : object1.entrySet()){
				if (!object2.has(entry.getKey())) return false;
			}
			return true;			
		}
		else return false;
	}
}
