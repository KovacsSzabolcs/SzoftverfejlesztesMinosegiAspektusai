package ro.sapientia.mesteri2015.test;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SCRUMAddToDoDefinition {

	protected WebDriver driver;
	protected int toDoCount=0;
	@Before
	public void setup() {
		driver = new FirefoxDriver();
	}

	@Given("^I open the first scrum story and click add new to do$")
	public void I_open_the_scrum_tool_add_page() throws Throwable {
		// Set implicit wait of 10 seconds and launch google
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.get("http://localhost:8080/");
		WebElement firstStory=driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[2]/div[1]/a"));
		firstStory.click();
		toDoCount=driver.findElements(By.cssSelector(".well-small")).size();
	}
	 
	@When("^I enter \"([^\"]*)\" in the to do title textbox and I push the add button$")
	public void I_enter_in_the_title_textbox_and_I_push_the_add_button(
			String additionTerms) throws Throwable {
		
		WebElement addButton =driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[2]/div[3]/a[2]"));
		addButton.click();
		
	
		
		WebElement titleTextBox = driver.findElement(By.id("todo-title"));
		titleTextBox.clear();
		titleTextBox.sendKeys(additionTerms);

	
		WebElement searchButton = driver.findElement(By.id("add-todo-button"));
		searchButton.click();
		
	}

	@Then("^I should get result \"([^\"]*)\" in to do list$")
	public void I_should_get_result_in_stories_list(String expectedResult)
			throws Throwable {
		
		int count = 0;
		count = driver.findElements(By.cssSelector(".well-small")).size();
		
		Assert.assertEquals(count, toDoCount+1);
		/*
		WebElement calculatorTextBox = driver.findElement(By.id("story-title"));
		String result = calculatorTextBox.getText();

		// Verify that result of 2+2 is 4
		Assert.assertEquals(result, expectedResult);
*/
	}

	@After
	public void closeBrowser() {
		driver.quit();
	}

}
