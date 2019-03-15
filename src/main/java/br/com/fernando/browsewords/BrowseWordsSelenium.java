package br.com.fernando.browsewords;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BrowseWordsSelenium {

    public static void main(String[] args) throws Exception {

        // FirefoxBinary firefoxBinary = new FirefoxBinary();
        // firefoxBinary.addCommandLineOptions("--headless");

        // System.setProperty("webdriver.firefox.driver","your path to the executable");
        System.setProperty("webdriver.firefox.driver", "C:\\Users\\fernando.romulo\\Development\\tools\\geckodriver\\geckodriver-0.24");

        // System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");

        // FirefoxOptions firefoxOptions = new FirefoxOptions();
        // firefoxOptions.setBinary(firefoxBinary);
        // FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        WebDriver driver = new FirefoxDriver();

        try {
            driver.get("http://www.google.com");
            driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
            
            WebElement queryBox = driver.findElement(By.name("q"));
            queryBox.sendKeys("headless firefox");
            
            WebElement searchBtn = driver.findElement(By.name("btnK"));
            searchBtn.click();
            
            WebElement iresDiv = driver.findElement(By.id("ires"));
            iresDiv.findElements(By.tagName("a")).get(0).click();
            
            
            
            System.out.println(driver.getPageSource());
        } finally {
            driver.quit();
        }
    }

}
