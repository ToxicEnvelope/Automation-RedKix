package com.redkix.automation.utils;

import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RedkixApp implements DriverSource {

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getenv("WEBDRIVER_CHROME_DRIVER"));
    }

    public WebDriver newDriver() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--start-fullscreen");

        //chromeOptions.put("binary", "path_to_exe");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("chromeOptions", options);
        capabilities.setBrowserName("chrome");

        String serverName = System.getenv("JENKINS_SERVER_NAME");
        if (serverName != null) {
            try {
                String serverPort = System.getenv("JENKINS_SERVER_PORT");
                if (serverPort == null) {
                    serverPort = "4444";
                }
                WebDriver driver = new RemoteWebDriver(new URL(String.format("http://%s:%s/wd/hub", serverName, serverPort)), capabilities);
                System.out.println("\n\n\n##### Remote selenium #####\n\n\n");
                return driver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver == null) {
            chromeDriver = System.getenv("WEBDRIVER_CHROME_DRIVER");
            if (chromeDriver == null) {
                chromeDriver = "D:/Oleg/tools/chromedriver/2.29/chromedriver.exe";
            }
        }
        System.setProperty("webdriver.chrome.driver", chromeDriver);

        System.out.println("\n\n\n##### Local selenium #####\n\n\n");

        return new ChromeDriver(capabilities);
    }

    public boolean takesScreenshots() {
        return true;
    }
}
