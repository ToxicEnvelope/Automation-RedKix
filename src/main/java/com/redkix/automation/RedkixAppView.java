package com.redkix.automation;


import com.google.common.base.Predicate;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.pages.PageObject;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

public abstract class RedkixAppView extends PageObject {

    public static final int WAIT_TIMEOUT = 60;
    public static final int OVERLAY_WAIT_TIMEOUT = 2;
    protected Logger LOGGER = LogManager.getLogger(this);
    protected By APP_CONTAINER = By.className("cfp-hotkeys");
    protected By MAIN_SPINNER = By.id("main-spinner");

    public RedkixAppView(WebDriver driver) {
        super(driver);
    }

    @Override
    public <T extends WebElementFacade> T element(By locator) {
        waitTillReady();
        return super.element(locator);
    }

    @Override
    public List<WebElementFacade> findAll(By bySelector) {
        waitTillReady();
        return super.findAll(bySelector);
    }

    protected void waitTillReady() {
//        waitForAjaxRequestsToFinish();
        waitForAngularRequestsToFinish();
        waitForOverlayToHide();
        waiter().until(ready());
    }

    protected void waitForMainLoaderToHide() {
        try {
            waiter().until((Predicate<WebDriver>) driver -> {
                try {
                    return driver.findElements(MAIN_SPINNER).size() == 0;
                }
                catch (Exception e) {
                    return true;
                }
            });
        }
        catch (TimeoutException e){}
    }

    protected void waitForOverlayToHide() {
        try {
            waiter(OVERLAY_WAIT_TIMEOUT).until(invisibilityOfElementLocated(By.className("ngdialog-overlay")));
        }
        catch (TimeoutException e){}
    }

    protected void waitForAjaxRequestsToFinish() {
        Predicate<WebDriver> ajaxCompletes = d -> {
            try {
                if (d == null) {
                    return true;
                }
                Boolean result = (Boolean) ((JavascriptExecutor)d).executeScript("return $.active === 0");
                return Boolean.TRUE.equals(result);
            }
            catch (WebDriverException e) {
                return false;
            }
        };

        waiter().until(ajaxCompletes);
    }


    @Override
    public void waitForAngularRequestsToFinish() {
        getDriver().manage().timeouts().setScriptTimeout(WAIT_TIMEOUT, TimeUnit.SECONDS);

        try {
            getJavascriptExecutorFacade().executeAsyncScript("var callback = arguments[arguments.length - 1]; " +
                    "if (typeof(angular) == \"undefined\") {callback()}; " +
                    "angular.element(document.querySelector('[ng-app]')).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);");

            LOGGER.debug("Angular requests completed");
        }
        catch (TimeoutException e) {
            LOGGER.debug("Angular requests wasn't completed");
        }
        catch (WebDriverException e) {
            LOGGER.debug("Exception while waiting for angular request to be completed: " + e.getMessage());
        }
    }

    protected Predicate<WebDriver> ready() {
        return d -> d.findElements(getViewIdentifier()).stream().anyMatch(WebElement::isDisplayed);
    }

    protected abstract By getViewIdentifier();

    protected void enterValue(By locator, String value, String fieldName) {
        WebElementFacade elementFacade = element(locator);
        waiter().until(ExpectedConditions.elementToBeClickable(elementFacade));
        elementFacade.clear();
        elementFacade.sendKeys(value);
        LOGGER.info("'{}' entered as {}", value, fieldName);
    }

    protected WebDriverWait waiter(int... timeout) {
        int actualTimeout = WAIT_TIMEOUT;
        if (timeout != null && timeout.length > 0) {
            actualTimeout = timeout[0];
        }
        return new WebDriverWait(getDriver(), actualTimeout);
    }

    public void openSpotlightByKeys() {
        waitTillReady();
        CharSequence commandKey = SystemUtils.IS_OS_MAC_OSX ? Keys.COMMAND : Keys.CONTROL;
        getDriver().switchTo().activeElement().sendKeys(commandKey, "9");
    }



}
