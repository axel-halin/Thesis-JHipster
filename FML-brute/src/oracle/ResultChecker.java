package oracle;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.xtext.util.Files;

/**
 * Handle all the necessary checks on the output logs.
 * 
 * @author Axel Halin
 * @author Alexandre Nuttinck
 */
public class ResultChecker {
	private String path = null;
	
	public ResultChecker(String path){
		this.path = path;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){return path;}
	
	/**
	 * Check the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	public boolean checkBuildApp(String fileName){
		try{
			String text = "";
	
			//extract log
			text = Files.readFileIntoString(path+fileName);
	
			//CHECK IF BUILD FAILED THEN false
			Matcher m = Pattern.compile("((.*?)APPLICATION FAILED TO START)").matcher(text);
			Matcher m2 = Pattern.compile("((.*?)BUILD FAILED)").matcher(text);
			Matcher m3 = Pattern.compile("((.*?)BUILD FAILURE)").matcher(text);
	
			while(m.find() | m2.find() | m3.find()) return false;
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Check the build result when using Docker.
	 * 
	 * @param fileName Log file containing the result of the build.
	 * @return True if the app successfully built; False otherwise.
	 */
	public boolean checkDockerBuild(String fileName){
		try{
			String text = Files.readFileIntoString(fileName);
			Matcher m = Pattern.compile("((.*?) Application 'jhipster' is running!)").matcher(text);
			while (m.find()) return true;
			return false;
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Return the time from log fils (compile.log, build.log)
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractTime(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("Started JhipsterApp in (.*?) seconds").matcher(text);
		Matcher m2 = Pattern.compile("Total time: (.*?) secs").matcher(text);

		String timebuild = "NOTFIND";

		while(m1.find())
		{
			return timebuild = m1.group(1).toString();
		}
		while(m2.find())
		{
			return timebuild = m2.group(1).toString();
		}

		return timebuild;
	}
	
	/**
	 * Return the memory used   
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractMemoryBuild(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);
		
		Matcher m1 = Pattern.compile("(.*?)Final Memory").matcher(text);

		String memoryBuild = "NOTFIND";

		while(m1.find())
		{
			return memoryBuild = m1.toString();
		}

		return memoryBuild;
	}
	

	/**
	 * Return results from tests ./mvnw clean test | ./gradlew clean test  
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractResultsTest(String fileName) throws FileNotFoundException{

		String text = "";
		text = Files.readFileIntoString(path+fileName);
		
		Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.repository.CustomSocialUsersConnectionRepositoryIntTest").matcher(text);
		Matcher m2 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.security.SecurityUtilsUnitTest").matcher(text);
		Matcher m3 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.SocialServiceIntTest").matcher(text);
		Matcher m4 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.UserServiceIntTest").matcher(text);
		Matcher m5 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AccountResourceIntTest").matcher(text);
		Matcher m6 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AuditResourceIntTest").matcher(text);
		Matcher m7 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.UserResourceIntTest").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find()|m2.find()|m3.find()|m4.find()|m5.find()|m6.find()|m7.find())
		{
			return resultsTests = m1.group().toString() +"\n"+ m2.group().toString() +"\n"+
					m3.group().toString() +"\n"+ m4.group().toString() +"\n"+ m5.group().toString() +"\n"+
					m6.group().toString() +"\n"+ m7.group().toString();
		}

		return resultsTests;
	}
	


	/**
	 * Return results from tests ./mvnw clean test | ./gradlew clean test -> cucumber
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractCucumber(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.cucumber.CucumberTest").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find())
		{
			return resultsTests = m1.group().toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from : gulp test 
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractKarmaJS(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("(.*?) FAILED").matcher(text);

		String resultsTests = "OK";

		while(m1.find())
		{
			return resultsTests = m1.group().toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from tests ./mvnw gatling:execute	| ./gradlew gatlingRun -x cleanResources
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 * TODO Create sequence of tests for gatling.
	 */
	public String extractGatling(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("(.*?)").matcher(text);

		String resultsTests = "NOTFIND";

		while(m1.find())
		{
			return resultsTests = m1.group(1).toString();
		}

		return resultsTests;
	}

	/**
	 * Return results from tests gulp protractor
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 * TODO Create sequence of tests for gatling.
	 */
	public String extractProtractor(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("(.*?) specs, (.*?) failures").matcher(text);
		
		String resultsTests = "NOTFIND";

		while(m1.find())
			{
			return resultsTests = m1.group().toString();
			}
		
		return resultsTests;
	}

	
	/**
	 * Return stacktraces
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of stacktraces
	 * 
	 */
	public String extractStacktraces(String fileName) throws FileNotFoundException{
		String text = "";

		//extract log
		text = Files.readFileIntoString(path+fileName);

		Matcher m1 = Pattern.compile("(Exception(.*?)\\n)").matcher(text);
		Matcher m2 = Pattern.compile("(Caused by(.*?)\\n)").matcher(text);
		
		String stacktraces = "";

		while(m1.find() | m2.find())
		{
			stacktraces = stacktraces + m1.group().toString();
			stacktraces = stacktraces + m2.group().toString();
		}

		return stacktraces;
	}
}
