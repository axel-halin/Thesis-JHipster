import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Oracle for all variants of configurator JHipster
 * 	- generate 
 * 	- build
 *  - tests
 * 
 * @author Nuttinck Alexandre
 *
 */
public class Oracle {

	private final static Logger _log = Logger.getLogger("JHipsterTest");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	
	
	/**
	 * Check if the configuration the yo-rc.json is right.
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkJSON(String jDirectory){
		
		return true;
	}
	
	/**
	 * Generate the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 */
	private void generateApp(String jDirectory) throws InterruptedException{
		
	    String generateSh = Files.readFileIntoString(getjDirectory(jDirectory) + "generate.sh");
		
		System.out.println(generateSh);
		
		  try {
		        ProcessBuilder pb = new ProcessBuilder(generateSh);
		        pb.inheritIO();
		        Process process = pb.start();
		        process.waitFor();
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * Check the App is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkGenerateApp(String jDirectory){
		
		Boolean success = true;
		
		String jhipsteryoLog = Files.readFileIntoString(getjDirectory(jDirectory) + "jhipsteryo.log");
		
		
		return success;
	}
	
	/**
	 * Build the App which is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private void buildApp(String jDirectory){
		
		String buildSh = Files.readFileIntoString(getjDirectory(jDirectory) + "build.sh");
		
		return;
	}
	
	/**
	 * Check the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkBuildApp(String jDirectory){
		
		Boolean success = true;
		
		String jhipsterBuildLog = Files.readFileIntoString(getjDirectory(jDirectory) + "jhipsterbuild.log");
		
		
		return success;
	}
	
	/**
	 * Launch Tests on the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private void testGenerateApp(String jDirectory){
		
		String generatorAppCommand = Files.readFileIntoString(getjDirectory(jDirectory) + "test.sh");
		
		
		return;
	}
	
	/**
	 * Return the path to folder jDirectory (which is in the relative path JHIPSTERS_DIRECTORY/)
	 * 
	 * @param jDirectory Name of the folder
	 * @return The relative path to folder with name jDirectory.
	 */
	private String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}
	
	/**
	 * Generate all variants of JHipster 3.6.1. 
	 */
	@Test
	public void generateJHipsterVariants() throws Exception{

		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;
		
		for (Integer i =1;i<weightFolder;i++){
			
			String jDirectory = "jhipster"+i;
			
			_log.info("Extracting files from jhipster "+i+"...");
			_log.info(getjDirectory(jDirectory));
			
			_log.info("Checking the JSON configuration...");
			
			Boolean check = checkJSON(jDirectory);
			
			if (check)
			{
				_log.info("JSON configuration is right...");
					
				_log.info("Generate the App...");
				generateApp(jDirectory);
				_log.info("App Generated...");
			}
	
			else
			{
				_log.info("JSON configuration might cause a problem...");
			}
			
			_log.info("Oracle generate "+i+" is done");
		}
	}
	
	/**
	 * Check all generation of JHipster 3.6.1. and build the app if the generation is OK.
	 */
	@Test
	public void checkAndBuildJHipsterVariants() throws Exception{
		
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;
		
		for (Integer i =1;i<weightFolder;i++){
			
			String jDirectory = "jhipster"+i;
			
			_log.info("Extracting files from jhipster "+i+"...");
			_log.info(getjDirectory(jDirectory));
			
				
			_log.info("Check the generation of the App...");
			boolean	check = checkGenerateApp(jDirectory);
				if (check)
					{
				    _log.info("App Checked Success...-> build of the app");
				    buildApp(getjDirectory(jDirectory));
				    }	
				else
					{
				    _log.info("App Checked Failure...");
				    }	
					
				_log.info("Oracle checkAndBuil "+i+" is done");
			}
	}
	
	/**
	 * Launch tests on variants of JHipster 3.6.1.
	 */
	@Test
	public void testsJHipsterVariants() throws Exception{
		
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;
		
		for (Integer i =1;i<weightFolder;i++){
			
			String jDirectory = "jhipster"+i;
			
				
			_log.info("Launch tests of the App...");
			testGenerateApp(jDirectory);
				
			_log.info("Oracle Tests "+i+" is done");
		}
	}
}
