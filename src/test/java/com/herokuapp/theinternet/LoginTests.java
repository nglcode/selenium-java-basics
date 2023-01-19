package com.herokuapp.theinternet;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
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

public class LoginTests {

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

	@AfterMethod(alwaysRun = true)
	private void tearDown() {
		driver.quit();
	}

	@Test(priority = 1, groups = { "positiveTests", "smokeTests" })
	public void positiveLoginTest() {

		System.out.println("Starting loginTest");

		String url = "https://the-internet.herokuapp.com/login";
		driver.get(url);
		System.out.println("Page is opened.");

		WebElement username = driver.findElement(By.id("username"));
		WebElement password = driver.findElement(By.name("password"));
		WebElement loginBtn = driver.findElement(By.xpath("//button[@class=\"radius\"]"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		username.sendKeys("tomsmith");
		password.sendKeys("SuperSecretPassword!");
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
		loginBtn.click();

		sleep(2);
		WebElement logoutBtn = driver.findElement(By.xpath("//a[@href='/logout']"));
		Assert.assertTrue(logoutBtn.isDisplayed(), "Logout button is not displayed");

		WebElement loggedDiv = driver.findElement(By.cssSelector("#flash"));
		String expectedMessage = "You logged into a secure area!";
		String actualMessage = loggedDiv.getText();
//		Assert.assertEquals(expectedMessage, actualMessage, "Success message is not the expected");
		Assert.assertTrue(actualMessage.contains(expectedMessage), "Success message is not the expected");

		String expectedUrl = "https://the-internet.herokuapp.com/secure";
		String actualUrl = driver.getCurrentUrl();
		Assert.assertEquals(expectedUrl, actualUrl, "Current URL is not the same as expected");

		sleep(2);
	}

	@Parameters({ "username", "password", "expectedMessage" })
	@Test(priority = 2, groups = { "negativeTests", "smokeTests" })
	public void negativeLoginTest(String username, String password, String expectedMessage) {

		System.out.println("Starting negativeLoginTest. Params: [" + username + " " + password + "]");

		String url = "https://the-internet.herokuapp.com/login";
		driver.get(url);
		System.out.println("Page is opened.");

		WebElement elemUsername = driver.findElement(By.id("username"));
		WebElement elemPassword = driver.findElement(By.name("password"));
		WebElement elemLoginBtn = driver.findElement(By.xpath("//button[@class=\"radius\"]"));

		elemUsername.sendKeys(username);
		elemPassword.sendKeys(password);
		elemLoginBtn.click();

		WebElement loggedDiv = driver.findElement(By.cssSelector("#flash"));
		String actualMessage = loggedDiv.getText();
		Assert.assertTrue(actualMessage.contains(expectedMessage), "Alert message is not the expected");

		String expectedUrl = "https://the-internet.herokuapp.com/login";
		String actualUrl = driver.getCurrentUrl();
		Assert.assertEquals(expectedUrl, actualUrl, "Current URL is not the same as expected");

		sleep(2);
	}

	private void sleep(int s) {
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
