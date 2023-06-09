package org.example.selenium;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverManager {


    public static FirefoxDriver getDriver(){
        System.setProperty("webdriver.gecko.driver", "/Applications/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        //TODO: Find non-deprecated solution
        //options.setHeadless(true);
        return new FirefoxDriver(options);
    }

    public static void closeDriver(RemoteWebDriver driver){
        driver.quit();
    }

}
