package com.herokuapp.theinternet;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ExceptionsTests {

	private WebDriver driver;

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	private void setUp(@Optional("chrome") String browser) {

		switch (browser) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		case "firefox":
			FirefoxOptions options = new FirefoxOptions();
			options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
			System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
			driver = new FirefoxDriver(options);
			break;
		default:
			System.out.println("Browser [" + browser + "] not found. Starting Chrome as default.");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		}

		sleep(3);
		driver.manage().window().maximize();
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));		
	}

	@Test(priority = 1)
	public void notVisibleTest() {

		driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
		WebElement btnStart = driver.findElement(By.cssSelector("div#start > button"));
		btnStart.click();

		WebElement finishElement = driver.findElement(By.cssSelector("div#finish"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(finishElement));
		String finishText = finishElement.getText();

		Assert.assertTrue(finishText.contains("Hello World!"), "Finish text: " + finishText);

	}

	@Test(priority = 2)
	public void timeoutTest() {

		driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
		WebElement btnStart = driver.findElement(By.cssSelector("div#start > button"));
		btnStart.click();

		WebElement finishElement = driver.findElement(By.cssSelector("div#finish"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		try {
			wait.until(ExpectedConditions.visibilityOf(finishElement));
		} catch (TimeoutException e) {
//			e.printStackTrace();
			System.out.println("Exception catched: " + e.getMessage());
			sleep(3);
		}
		String finishText = finishElement.getText();

		Assert.assertTrue(finishText.contains("Hello World!"), "Finish text: " + finishText);

	}

	@Test(priority = 3)
	public void noSuchElementTest() {

		driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");
		WebElement btnStart = driver.findElement(By.cssSelector("div#start > button"));
		btnStart.click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		/*
		 * WebElement finishElement =
		 * wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
		 * "div#finish")));
		 * 
		 * String finishText = finishElement.getText();
		 * 
		 * Assert.assertTrue(finishText.contains("Hello World!"), "Finish text: " +
		 * finishText);
		 */

		Assert.assertTrue(wait.until(
				ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div#finish"), "Hello World!")),
				"Couldn't verify expected text [Hello World!]");

	}
	
	@Test(priority = 3)
	public void staleElementTest() {
		
		driver.get("https://the-internet.herokuapp.com/dynamic_controls");
		
		WebElement checkbox = driver.findElement(By.id("checkbox"));
		WebElement btnRemove = driver.findElement(By.xpath("//button[contains(text(), 'Remove')]"));
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		btnRemove.click();
//		wait.until(ExpectedConditions.invisibilityOf(checkbox));
		
//		Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOf(checkbox)), "Checkbox is still visible");
		
		Assert.assertTrue(wait.until(ExpectedConditions.stalenessOf(checkbox)), "Checkbox is still visible");
		
		WebElement btnAdd = driver.findElement(By.xpath("//button[contains(text(), 'Add')]"));
		btnAdd.click();
		checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkbox")));
		Assert.assertTrue(checkbox.isDisplayed(), "Checkbox is not visible, but it should be."); 
		
	}
	
	@Test(priority = 4)
	public void disabledElementTest() {
		
		driver.get("https://the-internet.herokuapp.com/dynamic_controls");
		
		WebElement btnEnable = driver.findElement(By.xpath("//button[contains(text(), 'Enable')]"));
		WebElement inputField = driver.findElement(By.cssSelector("#input-example > input[type=text]"));
		
		String initialText = "FooBar";
		btnEnable.click();
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		inputField = wait.until(ExpectedConditions.elementToBeClickable(inputField));
		inputField.sendKeys(initialText);
		
		Assert.assertTrue(inputField.getAttribute("value").contains(initialText), "Field content is not the same as expected");
		
		
	}

	@AfterMethod(alwaysRun = true)
	private void tearDown() {
		driver.quit();
	}

	private void sleep(int s) {
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
