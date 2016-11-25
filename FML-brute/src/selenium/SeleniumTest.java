package selenium;

import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Script to populate the database using the user interface.
 * 
 * @author Axel Halin
 */
public class SeleniumTest {
	private static final Logger _log = Logger.getLogger("SeleniumTest");
	private static final long PAUSE_TIME = 2000;
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final String baseUrl;
	private String database = "";
	
	public SeleniumTest(String geckoDriverPath){
		//System.setProperty("webdriver.gecko.driver", geckoDriverPath);
		//driver = new FirefoxDriver();
		System.setProperty("phantomjs.binary.path", System.getProperty("user.dir")+"/phantomjs");
        driver = new PhantomJSDriver();
        driver.manage().window().setSize(new Dimension(1920,1080));
        
		baseUrl = "http://127.0.0.1:8080/#"; 
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver,5);
	}
	
	/**
	 * Launch a Firefox instance and simulate user interaction.
	 * This simulation create entities using the GUI generated via JHipster generator.
	 */
	public void populateDB(String database){
		this.database = database;
		try{
			driver.get(baseUrl);
			login("admin","admin");
			populateRegion();
			populateCountry();
			populateLocation();
			populateDepartment();
			populateTask();
			populateEmployee();
			//populateJob();
		} catch (Exception e){
			_log.error("Entities not fully populated\n"+e.getMessage());
		}
		//addJobHistories("2016-10-11 00:00", "2016-10-11 00:00", "FRENCH", "1004", "1006");
		driver.quit();
	}
	
	private void populateRegion(){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[2]/ul/li/a/span[2]"))).click();
		// Add regions
		addRegion("region1");		// ID = 1001
		addRegion("region2");		// ID = 1002
		addRegion("region3");		// ID = 1003
		addRegion("region4");		// ID = 1004
		addRegion("region5");		// ID = 1005
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addRegion("region6");		// ID = 1006
		addRegion("region7");		// ID = 1007
		addRegion("region8");		// ID = 1008
		addRegion("region9");		// ID = 1009
		addRegion("region10");		// ID = 1010
	}
	
	private void populateCountry(){
	    pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[2]/a/span[2]"))).click();
		// Add countries
		addCountry("country1",1);	// ID = 1011
		addCountry("country2",2);	// ...
		addCountry("country3",3);
		addCountry("country4",4);
		addCountry("country5",5);
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addCountry("country6",6);
		addCountry("country7",7);
		addCountry("country8",8);
		addCountry("country9",9);
		addCountry("country10",10);
	}
	
	private void populateLocation(){
		pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[3]/a/span[2]"))).click();
		// Add locations
		addLocation("address1", "postalCode1", "city1", "province1", 1);	// ID = 1021
		addLocation("address2", "postalCode2", "city2", "province2", 2);
		addLocation("address3", "postalCode3", "city3", "province3", 3);
		addLocation("address4", "postalCode4", "city4", "province4", 4);
		addLocation("address5", "postalCode5", "city5", "province5", 5);
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addLocation("address6", "postalCode6", "city6", "province6", 6);
		addLocation("address7", "postalCode7", "city7", "province7", 7);
		addLocation("address8", "postalCode8", "city8", "province8", 8);
		addLocation("address9", "postalCode9", "city9", "province9", 9);
		addLocation("address10", "postalCode10", "city10", "province10", 10);
	}
	
	private void populateDepartment(){
		pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[4]/a/span[2]"))).click();
		// Add departments
		addDepartment("department1", 1); // ID = 1031
		addDepartment("department2", 2);
		addDepartment("department3", 3);
		addDepartment("department4", 4);
		addDepartment("department5", 5);
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addDepartment("department6", 6);
		addDepartment("department7", 7);
		addDepartment("department8", 8);
		addDepartment("department9", 9);
		addDepartment("department10", 10);
	}
	
	private void populateTask(){
		pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[5]/a/span[2]"))).click();	
		// Add tasks
		addTask("task1","description1"); // ID = 1041
		addTask("task2","description2");
		addTask("task3","description3");
		addTask("task4","description4");
		addTask("task5","description5");
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addTask("task6","description6");
		addTask("task7","description7");
		addTask("task8","description8");
		addTask("task9","description9");
		addTask("task10","description10");
	}
	
	private void populateEmployee(){
		pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[6]/a/span[2]"))).click();
		// Add employees
		addEmployee("firstname1","lastname1","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 1, -1); // ID = 1051
		addEmployee("firstname2","lastname2","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 2, 1);
		addEmployee("firstname3","lastname3","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 3, -1);
		addEmployee("firstname4","lastname4","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 4, 2);
		addEmployee("firstname5","lastname5","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 5, -1);
		if(database.equals("\"mongodb\"") || database.equals("\"cassandra\"")) pause(PAUSE_TIME);
		addEmployee("firstname6","lastname6","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 6, 3);
		addEmployee("firstname7","lastname7","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 7, -1);
		addEmployee("firstname8","lastname8","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 8, 5);
		addEmployee("firstname9","lastname9","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 9, -1);
		addEmployee("firstname10","lastname10","test@test.com","0123456789","2016-10-11 00:00", "10", "10", 10, -1);
	}
	
	private void populateJob(){
		pause(PAUSE_TIME);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entity-menu"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[7]/a/span[2]"))).click();
	    // Add jobs
		addJob("job1", "5", "10", 1);
		addJob("job2", "5", "10", 2);
		addJob("job3", "5", "10", 3);
		addJob("job4", "5", "10", 4);
		addJob("job5", "5", "10", 5);
		addJob("job6", "5", "10", 6);
		addJob("job7", "5", "10", 7);
		addJob("job8", "5", "10", 8);
		addJob("job9", "5", "10", 9);
		addJob("job10", "5", "10", 10);
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
	   // wait.until(ExpectedConditions.elementToBeClickable(By.id("account-menu")));
		driver.findElement(By.id("account-menu")).click();
	    driver.findElement(By.xpath("//a[@id='login']/span[2]")).click();
	    
	    sendKeysByID("username",username,false);
	    sendKeysByID("password",password,false);
	    clickButton();
		pause(PAUSE_TIME);
	}
	
	/**
	 * Add a new Region entity via the GUI
	 * 
	 * @param regionName Name of the Region entity to add.
	 */
	private void addRegion(String regionName){
		clickButton();
		sendKeysByID("field_regionName", regionName, false);
		clickButton();
	}
	
	/**
	 * Add a new Country entity via the GUI
	 * 
	 * @param countryName Name of the Country entity to add.
	 * @param n Index of the element to select in dropdown list.
	 */
	private void addCountry(String countryName, int n){
		clickButton();
		sendKeysByID("field_countryName", countryName, false);
		// If SQL databases, select the value of foreign key
	    if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\"")){
	    	sendKeysByID("field_region", getTextOfNthOption("field_region", n), true);
	    }
	    
	    clickButton();
	}
	
	/**
	 * Add a new Location entity via the GUI
	 * 
	 * @param address Address of the Location entity
	 * @param postalCode Postal code of the entity
	 * @param city Name of the city of the entity
	 * @param stateProvince Name of the province of the Location
	 * @param n Index in the drop list of the Country entity
	 */
	private void addLocation(String address, String postalCode, String city, String stateProvince, int n){
		clickButton();
		sendKeysByID("field_streetAddress", address, false);
		sendKeysByID("field_postalCode", postalCode, false);
		sendKeysByID("field_city", city,false);
		sendKeysByID("field_stateProvince",stateProvince,false);
		
		if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\""))
			sendKeysByID("field_country", getTextOfNthOption("field_country", n), true);
		
		clickButton();
	}
	
	/**
	 * Add a new Department entity via the GUI
	 * 
	 * @param departmentName Name of the Department
	 * @param n Index in the drop list of the Location
	 */
	private void addDepartment(String departmentName, int n){
		clickButton();
		sendKeysByID("field_departmentName", departmentName,false);
	   
		if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\""))
			sendKeysByID("field_location", getTextOfNthOption("field_location", n), true);
		    
		clickButton();
	}
	
	/**
	 * Add a new Task entity via the GUI
	 * 
	 * @param title Title of the task
	 * @param description Description of the task.
	 */
	private void addTask(String title, String description){   
		clickButton();
		sendKeysByID("field_title", title,false);
		sendKeysByID("field_description", description,false);
		clickButton();
	}
	
	
	private void addEmployee(String firstName, String name, String email, String phone, 
				String date, String commission, String salary, int departmentIndex, int managerIndex)
	{
		clickButton();
	    sendKeysByID("field_firstName", firstName,false);
	    sendKeysByID("field_lastName", name,false);
	    sendKeysByID("field_email", email,false);
	    sendKeysByID("field_phoneNumber", phone,false);
	    sendKeysByID("field_commissionPct", commission,false);
	    sendKeysByID("field_salary", salary,false);
	    sendKeysByID("field_hireDate", date,false);
	    
	    if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\""))
	    	sendKeysByID("field_department", getTextOfNthOption("field_department", departmentIndex), true);
 	    
	    
	    if(managerIndex>0){
	    	if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\""))
	    		sendKeysByID("field_manager", getTextOfNthOption("field_manager", managerIndex), true);
	    }
	    
	    clickButton();
	}

	
	private void addJob(String title, String minSalary, String maxSalary, int employeeIndex){
		clickButton();
		sendKeysByID("field_jobTitle", title, false);
		sendKeysByID("field_minSalary", minSalary, false);
		sendKeysByID("field_maxSalary", maxSalary, false);
		
	    if(!database.equals("\"mongodb\"") && !database.equals("\"cassandra\""))
			sendKeysByID("field_employee", getTextOfNthOption("field_employee", employeeIndex), true);
			
	    clickButton();
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
	
	
	/**
	 * Method to simulate user interaction with button of css class 'button.btn.btn-primary'.
	 * This button is either the new entity button or the valid entity button.
	 */
	private void clickButton(){
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn.btn-primary")));
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	}
	
	/**
	 * Method to simulate user interaction with an input field of the interface.
	 * The input field is identified by its id, and the desired value is passed when the element is visible.
	 * 
	 * @param id Id of the input field.
	 * @param value Value to input.
	 */
	private void sendKeysByID(String id, String value, boolean dropDown){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		if(!dropDown) driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(value);
	}
	
	private String getTextOfNthOption(String id, int n){
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		Select select = new Select(driver.findElement(By.id(id)));
		return select.getOptions().get(n).getText();
	}
}