package oracle;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles the extraction of Cucumber tests results.
 * These results are then written in a CSV file
 * 
 * @author Axel Halin
 */
public class CucumberResultExtractor {
	
	// Common test result files
	private static final String FILE1 = "TEST-io.variability.jhipster.security.SecurityUtilsUnitTest.xml";
	private static final String FILE2 = "TEST-io.variability.jhipster.service.UserServiceIntTest.xml";
	private static final String FILE3 = "TEST-io.variability.jhipster.web.rest.AccountResourceIntTest.xml";
	private static final String FILE4 = "TEST-io.variability.jhipster.web.rest.AuditResourceIntTest.xml";	
	private static final String FILE5 = "TEST-io.variability.jhipster.web.rest.UserResourceIntTest.xml";
	// Entities dependent test result files
	private static final String ENTITY1 = "TEST-io.variability.jhipster.web.rest.CountryResourceIntTest.xml";
	private static final String ENTITY2 = "TEST-io.variability.jhipster.web.rest.DepartmentResourceIntTest.xml";
	private static final String ENTITY3 = "TEST-io.variability.jhipster.web.rest.EmployeeResourceIntTest.xml";
	private static final String ENTITY4 = "TEST-io.variability.jhipster.web.rest.JobHistoryResourceIntTest.xml";
	private static final String ENTITY5 = "TEST-io.variability.jhipster.web.rest.JobResourceIntTest.xml";
	private static final String ENTITY6 = "TEST-io.variability.jhipster.web.rest.LocationResourceIntTest.xml";
	private static final String ENTITY7 = "TEST-io.variability.jhipster.web.rest.RegionResourceIntTest.xml";
	private static final String ENTITY8 = "TEST-io.variability.jhipster.web.rest.TaskResourceIntTest.xml";
	
	
	private String path;
	private String buildTool;
	private final String CUCUMBER_PATH_MAVEN = "target/surefire-reports/";
	private final String CUCUMBER_PATH_GRADLE = "build/test-results/";
	private static final int NUMBER_ENTITIES = 8;
	private String[] result = new String[29+(6*NUMBER_ENTITIES)+1];
	
	public CucumberResultExtractor(String path, String buildTool){
		this.path = path;
		this.buildTool = buildTool;
		for(int i=0; i<result.length;i++) result[i] = "ND";
	}
	
	private static final Logger _log = Logger.getLogger("CucumberResultExtractor");
	
	/**
	 * Extract the result of Cucumber tests. The result is the time needed to execute common (CRUD) operations.
	 * 
	 * @param fileName Name of the result file
	 * @param entityName Name of the entity tested
	 * @return 
	 * TODO 6 result: update, create, get, getNotExist, delete, getAll
	 */
	public String[] extractEntityCucumberTest(){
		// TODO cucumberpath = Maven|Gradle depending....
		String cucumberPath;
		if(buildTool.equals("maven")) cucumberPath = CUCUMBER_PATH_MAVEN;
		else cucumberPath = CUCUMBER_PATH_GRADLE;
		// Extracting from common files
		extractResultFromFile(path+cucumberPath+FILE1,0); 		// 3 results
		extractResultFromFile(path+cucumberPath+FILE2,3);		// 7 results
		extractResultFromFile(path+cucumberPath+FILE3,10);		// 12 results
		extractResultFromFile(path+cucumberPath+FILE4,22);		// 5 results
		extractResultFromFile(path+cucumberPath+FILE5,27);		// 2 results
		// Extracting from user defined entities
		extractResultFromFile(path+cucumberPath+ENTITY1,29); 	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY2,35); 	// 7 results
		extractResultFromFile(path+cucumberPath+ENTITY3,42);	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY4,48);	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY5,54);	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY6,60);	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY7,66);	// 6 results
		extractResultFromFile(path+cucumberPath+ENTITY8,72);	// 6 results
		
		return result;
	}
	
	
	private void extractResultFromFile(String fileName, int startingIndex){
		try{
			File xmlFile = new File(fileName);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nodeList = doc.getElementsByTagName("testcase");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					result[startingIndex] = eElement.getAttribute("time");
					startingIndex++;
				}
			}
		} catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}
}

