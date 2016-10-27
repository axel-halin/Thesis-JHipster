package selenium;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SeleniumTest {
	private static final Logger _log = Logger.getLogger("SeleniumTest");
	private static final long PAUSE_TIME = 1000;
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
		addRegion("region1");
		addCountry("country1","1001");
		addLocation("address1", "postalCode1", "city1", "province1", "1002");
		addDepartment("department1", "1003");
		addTask("task1","description");
		addEmployee("test", "test", "test", "test", "2016-10-11 00:00", "5", "5", "1004", null);
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