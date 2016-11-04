package oracle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;


/**
 * Handle all the necessary checks on the output logs.
 * 
 * @author Axel Halin
 * @author Alexandre Nuttinck
 */
public class ResultChecker {
	private String path = null;
	private static final Logger _log = Logger.getLogger("ResultChecker");
	private String JACOCOPATH = "target/test-results/coverage/jacoco/";
	private static final String DEFAULT_NOT_FOUND_VALUE = "ND";

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
			String text = Files.readFileIntoString(path+fileName);

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
	public String extractTime(String fileName){
		String timebuild = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);

			Matcher m1 = Pattern.compile("Started JhipsterApp in (.*?) seconds").matcher(text);
			Matcher m2 = Pattern.compile("Total time: (.*?) secs").matcher(text);
			Matcher m3 = Pattern.compile("Total time: (.*?)s").matcher(text);
			Matcher m4 = Pattern.compile("Total time: (.*?) mins (.*?) secs").matcher(text);
			Matcher m5 = Pattern.compile("Total time: (.*?) hrs (.*?) mins (.*?) secs").matcher(text);
			Matcher m6 = Pattern.compile("Total time: (.*?):(.*?) min").matcher(text);

			//check if hrs 
			while(m5.find()) return timebuild = Float.toString(((Float.valueOf(m5.group(1).toString()) *3600)+ (Float.valueOf(m5.group(2).toString())*60) + Float.valueOf(m5.group(3).toString())));
			//check if mins
			while(m4.find()) return timebuild = Float.toString(((Float.valueOf(m4.group(1).toString())*60) + Float.valueOf(m4.group(2).toString())));
			//check if min
			while(m6.find()) return timebuild = Float.toString(((Float.valueOf(m6.group(1).toString())*60) + Float.valueOf(m6.group(2).toString())));
			//check if secs
			while(m2.find()) return timebuild = m2.group(1).toString();
			//check if seconds
			while(m1.find()) return timebuild = m1.group(1).toString();
			//check if s
			while(m3.find()) return timebuild = m3.group(1).toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
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
	public String extractMemoryBuild(String fileName){
		String memoryBuild = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);
			Matcher m1 = Pattern.compile("(.*?)Final Memory").matcher(text);
			while(m1.find()) return memoryBuild = m1.toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
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
	public String extractResultsTest(String fileName){
		String resultsTests = "";
		try{
			String text = Files.readFileIntoString(path+fileName);

			Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.repository.CustomSocialUsersConnectionRepositoryIntTest").matcher(text);
			Matcher m2 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.security.SecurityUtilsUnitTest").matcher(text);
			Matcher m3 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.SocialServiceIntTest").matcher(text);
			Matcher m4 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.service.UserServiceIntTest").matcher(text);
			Matcher m5 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AccountResourceIntTest").matcher(text);
			Matcher m6 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.AuditResourceIntTest").matcher(text);
			Matcher m7 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.web.rest.UserResourceIntTest").matcher(text);

			while(m1.find()) resultsTests = resultsTests + m1.group().toString() +"\n";
			while(m2.find()) resultsTests = resultsTests + m2.group().toString() +"\n";
			while(m3.find()) resultsTests = resultsTests + m3.group().toString() +"\n";
			while(m4.find()) resultsTests = resultsTests + m4.group().toString() +"\n";
			while(m5.find()) resultsTests = resultsTests + m5.group().toString() +"\n";
			while(m6.find()) resultsTests = resultsTests + m6.group().toString() +"\n";
			while(m7.find()) resultsTests = resultsTests + m7.group().toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		if(resultsTests.equals("")) return DEFAULT_NOT_FOUND_VALUE;
		else return resultsTests;
	}



	/**
	 * Return results from tests ./mvnw clean test | ./gradlew clean test -> cucumber
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractCucumber(String fileName){
		String resultsTests = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);
			Matcher m1 = Pattern.compile("Tests run: (.*?) - in io.variability.jhipster.cucumber.CucumberTest").matcher(text);
			while(m1.find()) return resultsTests = m1.group().toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
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
	public String extractKarmaJS(String fileName){
		String resultsTests = "OK";
		try{
			String text = Files.readFileIntoString(path+fileName);
			Matcher m1 = Pattern.compile("(.*?) FAILED").matcher(text);
			while(m1.find()) 
			{resultsTests = resultsTests + m1.group().toString() + "\n";}
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
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
	public String extractGatling(String fileName){
		String resultsTests = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);

			Matcher m1 = Pattern.compile("(.*?)").matcher(text);
			while(m1.find()) return resultsTests = m1.group(1).toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return resultsTests;
	}

	/**
	 * Return results from tests gulp protractor
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of time of building
	 * 
	 */
	public String extractProtractor(String fileName){
		String resultsTests = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);

			Matcher m1 = Pattern.compile("(.*?) specs, (.*?) failures").matcher(text);
			
			while(m1.find()) return resultsTests = m1.group().toString();
		} catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return resultsTests;
	}
	
	/**
	 * Return coverageInstructions from Jacoco
	 * 
	 * @param jDirectory Name of the folder
	 * @return String coverage of instructions
	 * 
	 */
	public String extractCoverageIntstructions(String fileName){
		String resultsTests = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+JACOCOPATH+fileName);
	
			Matcher m1 = Pattern.compile("Total</td><td class=\"bar\">(.*?)</td><td class=\"ctr2\">(.*?) %"
					+ "</td><td class=\"bar\">(.*?)</td><td class=\"ctr2\">(.*?) %</td>").matcher(text);
			
			while(m1.find()) return resultsTests = m1.group(2).toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return resultsTests;
	}
	
	/**
	 * Return coverageBranches from Jacoco
	 * 
	 * @param jDirectory Name of the folder
	 * @return String coverage of Branches
	 * 
	 */
	public String extractCoverageBranches(String fileName){
		String resultsTests = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+JACOCOPATH+fileName);
	
			Matcher m1 = Pattern.compile("Total</td><td class=\"bar\">(.*?)</td><td class=\"ctr2\">(.*?) %"
					+ "</td><td class=\"bar\">(.*?)</td><td class=\"ctr2\">(.*?) %</td>").matcher(text);
	
			while(m1.find()) return resultsTests = m1.group(4).toString();
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return resultsTests;
	}

	/**
	 * Retrieves the percentage of Javascript Statements Coverage
	 * 
	 * @param fileName File containing the percentage. 
	 * @return The percentage of Statement Coverage (Javascript)
	 */
	public String extractJSCoverageStatements(String fileName){
		String result = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);
			Matcher m1 = Pattern.compile("(.*?)<div class='fl pad1y space-right2'>(\r*?)(\n*?)"
										+ "(.*?)<span class=\"strong\">(.*?)</span>(\r*?)(\n*?)"
										+ "(.*?)<span class=\"quiet\">Statements</span>(\r*?)(\n*?)"
										+ "(.*?)<span class='fraction'>(.*?)</span>(\r*?)(\n*?)"
										+ "(.*?)</div>").matcher(text);
			while(m1.find()) return m1.group(5).toString();
		} catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return result;
	}

	/**
	 * Retrieves the percentage of Javascript Branches Coverage
	 * 
	 * @param fileName File containing the percentage.
	 * @return The percentage of Branches Coverage (Javascript)
	 */
	public String extractJSCoverageBranches(String fileName){
		String result = DEFAULT_NOT_FOUND_VALUE;
		try{
			String text = Files.readFileIntoString(path+fileName);
			Matcher m1 = Pattern.compile("(.*?)<div class='fl pad1y space-right2'>(\r*?)(\n*?)"
										+ "(.*?)<span class=\"strong\">(.*?)</span>(\r*?)(\n*?)"
										+ "(.*?)<span class=\"quiet\">Branches</span>(\r*?)(\n*?)"
										+ "(.*?)<span class='fraction'>(.*?)</span>(\r*?)(\n*?)"
										+ "(.*?)</div>").matcher(text);
			while(m1.find()) return m1.group(5).toString();
		} catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return result;
	}

	/**
	 * Return stacktraces
	 * 
	 * @param jDirectory Name of the folder
	 * @return String of stacktraces
	 * 
	 */
	public String extractStacktraces(String fileName){
		String stacktraces = "";
		try{		
			String text = Files.readFileIntoString(path+fileName);
	
			Matcher m1 = Pattern.compile("(Exception(.*?)\\n)").matcher(text);
			Matcher m2 = Pattern.compile("(Caused by(.*?)\\n)").matcher(text);
			Matcher m3 = Pattern.compile("((.*?)\\[ERROR\\](.*))").matcher(text);
	
			while(m1.find()) stacktraces = stacktraces + m1.group().toString() + "\n";
			while(m2.find()) stacktraces = stacktraces + m2.group().toString() + "\n";
			while(m3.find()) stacktraces = stacktraces + m3.group(3).toString() + "\n";
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
		return stacktraces;
	}
}
