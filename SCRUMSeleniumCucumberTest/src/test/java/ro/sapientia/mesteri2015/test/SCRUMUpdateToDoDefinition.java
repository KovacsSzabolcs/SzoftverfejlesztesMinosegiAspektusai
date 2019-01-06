package ro.sapientia.mesteri2015.test;

import java.util.List;
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

public class SCRUMUpdateToDoDefinition {

	protected WebDriver driver;
	protected int toDoCount=0;
	@Before
	public void setup() {
		driver = new FirefoxDriver();
	}

	@Given("^I edit the scrum list's first story's first to do$")
	public void I_open_the_scrum_tool_add_page() throws Throwable {
		// Set implicit wait of 10 seconds and launch google
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.get("http://localhost:8080/");
		WebElement firstStory=driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[2]/div[1]/a"));
		firstStory.click();
		//toDoCount=driver.findElements(By.cssSelector(".well-small")).size();
		WebElement todo = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[2]/div[2]/div[1]/a"));
		todo.click();
		
		
		
	}
	
	 
	@When("^I enter \"([^\"]*)\" in the update title textbox and I push the add button$")
	public void I_enter_in_the_title_textbox_and_I_push_the_add_button(
			String additionTerms) throws Throwable {
		
		WebElement updateButton =driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div[2]/div[2]/a[1]"));
		updateButton.click();
		
	
		
		WebElement titleTextBox = driver.findElement(By.id("todo-title"));
		titleTextBox.clear();
		titleTextBox.sendKeys(additionTerms);

	
		WebElement update = driver.findElement(By.xpath("//*[@id=\"update-todo-button\"]"));
		update.click();
		
		
	}

	@Then("^I should get updated result \"([^\"]*)\" in to do list$")
	public void I_should_get_result_in_stories_list(String expectedResult)
			throws Throwable {
		
		
		WebElement calculatorTextBox = driver.findElement(By.xpath("//*[@id=\"todo-title\"]"));
		String result = calculatorTextBox.getText();

		// Verify that result of 2+2 is 4
		Assert.assertEquals(result, expectedResult);

	}

	@After
	public void closeBrowser() {
		driver.quit();
	}

}
