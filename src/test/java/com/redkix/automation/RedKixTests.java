package com.redkix.automation;


import net.thucydides.core.annotations.Managed;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RedKixTests {

    private Logger LOGGER = LogManager.getLogger(this);

    @Managed
    private WebDriver driver;

    @Before
    public void setUp() {
        RemoteWebDriver d = (RemoteWebDriver) ((WebDriverFacade) driver).getProxiedDriver();
        Capabilities caps = d.getCapabilities();
        LOGGER.debug("Browser name: {}", caps.getBrowserName());
        LOGGER.debug("Browser version: {}", caps.getVersion());
        LOGGER.debug("Platform: {}", caps.getPlatform());
    }

}
