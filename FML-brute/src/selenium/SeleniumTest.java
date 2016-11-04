package selenium;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Script to populate the database using the user interface.
 * 
 * @author Axel Halin
 */
public class SeleniumTest {
	private static final Logger _log = Logger.getLogger("SeleniumTest");
	private static final long PAUSE_TIME = 1400;
	private final WebDriver driver;
	private final String baseUrl;
	
	public SeleniumTest(){
		// TODO change path
		System.setProperty("webdriver.gecko.driver", "/home/axel/Eclipse/Thesis-JHipster/Dependencies/geckodriver");
		driver = new FirefoxDriver();
		baseUrl = "http://127.0.0.1:8080/#"; 
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	/**
	 * Launch a Firefox instance and simulate user interaction.
	 * This simulation create entities using the GUI generated via JHipster generator.
	 */
	public void populateDB(){
		driver.get(baseUrl);
		login("admin","admin");
		// Add regions
		addRegion("region1");		// ID = 1001
		addRegion("region2");		// ID = 1002
		addRegion("region3");		// ID = 1003
		addRegion("region4");		// ID = 1004
		addRegion("region5");		// ID = 1005
		addRegion("region6");		// ID = 1006
		addRegion("region7");		// ID = 1007
		addRegion("region8");		// ID = 1008
		addRegion("region9");		// ID = 1009
		addRegion("region10");		// ID = 1010
		// Add countries
		addCountry("country1","1001");	// ID = 1011
		addCountry("country2","1002");	// ...
		addCountry("country3","1003");
		addCountry("country4","1004");
		addCountry("country5","1005");
		addCountry("country6","1006");
		addCountry("country7","1007");
		addCountry("country8","1008");
		addCountry("country9","1009");
		addCountry("country10","1010");
		// Add locations
		addLocation("address1", "postalCode1", "city1", "province1", "1011");	// ID = 1021
		addLocation("address2", "postalCode2", "city2", "province2", "1012");
		addLocation("address3", "postalCode3", "city3", "province3", "1013");
		addLocation("address4", "postalCode4", "city4", "province4", "1014");
		addLocation("address5", "postalCode5", "city5", "province5", "1015");
		addLocation("address6", "postalCode6", "city6", "province6", "1016");
		addLocation("address7", "postalCode7", "city7", "province7", "1017");
		addLocation("address8", "postalCode8", "city8", "province8", "1018");
		addLocation("address9", "postalCode9", "city9", "province9", "1019");
		addLocation("address10", "postalCode10", "city10", "province10", "1020");
		// Add departments
		addDepartment("department1", "1021"); // ID = 1031
		addDepartment("department2", "1022");
		addDepartment("department3", "1023");
		addDepartment("department4", "1024");
		addDepartment("department5", "1025");
		addDepartment("department6", "1026");
		addDepartment("department7", "1027");
		addDepartment("department8", "1028");
		addDepartment("department9", "1029");
		addDepartment("department10", "1030");
		// Add tasks
		addTask("task1","description1"); // ID = 1041
		addTask("task2","description2");
		addTask("task3","description3");
		addTask("task4","description4");
		addTask("task5","description5");
		addTask("task6","description6");
		addTask("task7","description7");
		addTask("task8","description8");
		addTask("task9","description9");
		addTask("task10","description10");
		// Add employees
		addEmployee("firstname1","lastname1","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1031", null); // ID = 1051
		addEmployee("firstname2","lastname2","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1032", "1051");
		addEmployee("firstname3","lastname3","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1033", null);
		addEmployee("firstname4","lastname4","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1034", "1052");
		addEmployee("firstname5","lastname5","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1035", null);
		addEmployee("firstname6","lastname6","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1036", "1053");
		addEmployee("firstname7","lastname7","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1037", null);
		addEmployee("firstname8","lastname8","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1038", "1055");
		addEmployee("firstname9","lastname9","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1039", null);
		addEmployee("firstname10","lastname10","test@test.com","0123456789","2016-10-11 00:00", "10", "10", "1040", null);
		//addJob("job1", "5", "10", "1005");
		//addJobHistories("2016-10-11 00:00", "2016-10-11 00:00", "FRENCH", "1004", "1006");
		driver.quit();
	}
	
	/**
	 * Pause the execution for "time" milliseconds.
	 * 
	 * @param time Time of the pause.
	 */
	private void pause(long time){
		try{
			Thread.sleep(time);
		} catch(Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}
	
	/**
	 * Handles the login to the JHipster App.
	 * 
	 * @param username Username with which to log
	 * @param password Password of the account
	 */
	private void login(String username, String password){
	    driver.findElement(By.id("account-menu")).click();
	    driver.findElement(By.xpath("//a[@id='login']/span[2]")).click();
	    driver.findElement(By.id("username")).clear();
	    driver.findElement(By.id("username")).sendKeys(username);
	    driver.findElement(By.id("password")).clear();
	    driver.findElement(By.id("password")).sendKeys(password);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
		pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Region entity via the GUI
	 * 
	 * @param regionName Name of the Region entity to add.
	 */
	private void addRegion(String regionName){
	    driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[2]/ul/li/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_regionName")).clear();
	    driver.findElement(By.id("field_regionName")).sendKeys(regionName);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Country entity via the GUI
	 * 
	 * @param countryName Name of the Country entity to add.
	 * @param regionID Id number of the related Region entity.
	 */
	private void addCountry(String countryName, String regionID){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[2]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_countryName")).clear();
	    driver.findElement(By.id("field_countryName")).sendKeys(countryName);
	    // Sendkeys to select option from dropdown list (won't work with Select class)
	    driver.findElement(By.id("field_region")).sendKeys(regionID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();;
	    pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Location entity via the GUI
	 * 
	 * @param address Address of the Location entity
	 * @param postalCode Postal code of the entity
	 * @param city Name of the city of the entity
	 * @param stateProvince Name of the province of the Location
	 * @param countryID Id number of the matching Country entity
	 */
	private void addLocation(String address, String postalCode, String city, String stateProvince, String countryID){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[3]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_streetAddress")).clear();
	    driver.findElement(By.id("field_streetAddress")).sendKeys(address);
	    driver.findElement(By.id("field_postalCode")).clear();
	    driver.findElement(By.id("field_postalCode")).sendKeys(postalCode);
	    driver.findElement(By.id("field_city")).clear();
	    driver.findElement(By.id("field_city")).sendKeys(city);
	    driver.findElement(By.id("field_stateProvince")).clear();
	    driver.findElement(By.id("field_stateProvince")).sendKeys(stateProvince);
	    driver.findElement(By.id("field_country")).sendKeys(countryID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Department entity via the GUI
	 * 
	 * @param departmentName Name of the Department
	 * @param locationID Id number of the related Location entity
	 */
	private void addDepartment(String departmentName, String locationID){
	    // Adding new Department entities
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[4]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_departmentName")).clear();
	    driver.findElement(By.id("field_departmentName")).sendKeys(departmentName);
	    driver.findElement(By.id("field_location")).sendKeys(locationID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Task entity via the GUI
	 * 
	 * @param title Title of the task
	 * @param description Description of the task.
	 */
	private void addTask(String title, String description){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[5]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_title")).clear();
	    driver.findElement(By.id("field_title")).sendKeys(title);
	    driver.findElement(By.id("field_description")).clear();
	    driver.findElement(By.id("field_description")).sendKeys(description);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	private void addEmployee(String firstName, String name, String email, String phone, 
				String date, String commission, String salary, String departmentID, String managerID){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[6]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_firstName")).clear();
	    driver.findElement(By.id("field_firstName")).sendKeys(firstName);
	    driver.findElement(By.id("field_lastName")).clear();
	    driver.findElement(By.id("field_lastName")).sendKeys(name);
	    driver.findElement(By.id("field_email")).clear();
	    driver.findElement(By.id("field_email")).sendKeys(email);
	    driver.findElement(By.id("field_phoneNumber")).clear();
	    driver.findElement(By.id("field_phoneNumber")).sendKeys(phone);
	    driver.findElement(By.id("field_hireDate")).clear();
	    driver.findElement(By.id("field_hireDate")).sendKeys(date);
	    driver.findElement(By.id("field_commissionPct")).clear();
	    driver.findElement(By.id("field_commissionPct")).sendKeys(commission);
	    driver.findElement(By.id("field_salary")).clear();
	    driver.findElement(By.id("field_salary")).sendKeys(salary);
	    driver.findElement(By.id("field_department")).sendKeys(departmentID);
	    if(managerID!=null) driver.findElement(By.id("field_manager")).sendKeys(managerID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	private void addJob(String title, String minSalary, String maxSalary, String employeeID){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[7]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_jobTitle")).clear();
	    driver.findElement(By.id("field_jobTitle")).sendKeys(title);
	    driver.findElement(By.id("field_minSalary")).clear();
	    driver.findElement(By.id("field_minSalary")).sendKeys(minSalary);
	    driver.findElement(By.id("field_maxSalary")).clear();
	    driver.findElement(By.id("field_maxSalary")).sendKeys(maxSalary);
	    driver.findElement(By.id("field_employee")).sendKeys(employeeID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
	
	private void addJobHistories(String start, String end, String language, String departmentID, String employeeID){
		driver.findElement(By.id("entity-menu")).click();
	    driver.findElement(By.xpath("//li[8]/a/span[2]")).click();
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("field_startDate")).clear();
	    driver.findElement(By.id("field_startDate")).sendKeys(start);
	    driver.findElement(By.id("field_endDate")).clear();
	    driver.findElement(By.id("field_endDate")).sendKeys(end);
	    driver.findElement(By.id("field_language")).sendKeys(language);
	    driver.findElement(By.id("field_department")).sendKeys(departmentID);
	    driver.findElement(By.id("field_employee")).sendKeys(employeeID);
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    pause(PAUSE_TIME);
	}
}