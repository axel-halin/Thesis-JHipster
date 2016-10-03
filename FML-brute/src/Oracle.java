import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private String projectDirectory = System.getProperty("user.dir");

	public void executeCommands() throws IOException {

		File tempScript = createTempScript();

		try {
			ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
			pb.inheritIO();
			Process process = pb.start();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			tempScript.delete();
		}
	}

	public File createTempScript() throws IOException {
		File tempScript = File.createTempFile("script", null);

		Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
				tempScript));
		PrintWriter printWriter = new PrintWriter(streamWriter);

		printWriter.println("#!/bin/bash");
		printWriter.println("cd bin");
		printWriter.println("ls");

		printWriter.close();

		return tempScript;
	}

	/**
	 * Generate the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void generateApp(String jDirectory) throws InterruptedException, IOException{
		try {
			ProcessBuilder pb2 = new ProcessBuilder("./generate.sh");
			pb2.directory(new File(projectDirectory + "/" + getjDirectory(jDirectory) +"/"));
			//pb2.inheritIO();
			Process process = pb2.start();
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
	private boolean checkGenerateApp(String jDirectory) throws FileNotFoundException{

		String text = "";
		
		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "jhipsteryo.log");

		//CHECK IF Server app generated successfully.
		//OR Client app generated successfully.
		Matcher m = Pattern.compile("((.*?)Server app generated successfully.)").matcher(text);
		Matcher m2 = Pattern.compile("((.*?)Client app generated successfully.)").matcher(text);

		while(m.find() | m2.find())

		{
			return true;
		} 

		return false;
	}

	/**
	 * Build the App which is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private void buildApp(String jDirectory){

		//String buildSh = Files.readFileIntoString(getjDirectory(jDirectory) + "build.sh");

		return;
	}

	/**
	 * Check the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private boolean checkBuildApp(String jDirectory) throws FileNotFoundException{
		
		String text = "";
		
		//extract log
		text = Files.readFileIntoString(getjDirectory(jDirectory) + "jhipsterbuild.log");


		//CHECK IF APPLICATION FAILED TO START THEN false
		Matcher m = Pattern.compile("((.*?)APPLICATION FAILED TO START)").matcher(text);

		while(m.find())

		{
			return false;

		} 

		return true;
	}

	/**
	 * Launch Tests on the App is build successfully
	 * 
	 * @param jDirectory Name of the folder
	 */
	private void testGenerateApp(String jDirectory){

		//String generatorAppCommand = Files.readFileIntoString(getjDirectory(jDirectory) + "test.sh");


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

		// weight of Folder Jhipsters
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;

		for (Integer i =1;i<=weightFolder;i++){

			String jDirectory = "jhipster"+i;

			_log.info("Extracting files from jhipster "+i+"...");
			_log.info(getjDirectory(jDirectory));

			_log.info("Generate the App...");
			generateApp(jDirectory);
			_log.info("App Generated...");

			_log.info("Oracle generate "+i+" is done");
		}
	}

	/**
	 * Check all generation of JHipster 3.6.1. and build the app if the generation is OK.
	 */
	//@Test
	public void checkAndBuildJHipsterVariants() throws Exception{

		// weight of Folder Jhipsters
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;

		for (Integer i =1;i<=weightFolder;i++){

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
				_log.info("App generation Failure...");
			}	

			_log.info("Oracle checkAndBuil "+i+" is done");
		}
	}

	/**
	 * Launch tests on variants of JHipster 3.6.1.
	 */
	//@Test
	public void testsJHipsterVariants() throws Exception{

		// weight of Folder Jhipsters
		File folder = new File(getjDirectory(""));
		Integer weightFolder = folder.list().length;

		for (Integer i =1;i<=weightFolder;i++){

			String jDirectory = "jhipster"+i;

			_log.info("Check the build ...");
			boolean	check = checkBuildApp(jDirectory);
			if (check)
			{
				_log.info("Build Success -> Launch tests of the App...");
				testGenerateApp(jDirectory);
			}	
			else
			{
				_log.info("App Build Failure...");
			}	

			_log.info("Oracle Tests "+i+" is done");
		}
	}
}
