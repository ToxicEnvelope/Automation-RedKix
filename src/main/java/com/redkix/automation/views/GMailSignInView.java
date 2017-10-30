package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class GMailSignInView extends RedkixAppView {

    private static final By GMAIL_VIEW_CONTAINER = By.id("view_container");

    private static final By EMAIL_INPUT = By.id("identifierId");
    private static final By EMAIL_NEXT_BUTTON = By.id("identifierNext");

    private static final By PASSWORD_INPUT = By.name("password");
    private static final By PASSWORD_NEXT_BUTTON = By.id("passwordNext");

    public GMailSignInView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return GMAIL_VIEW_CONTAINER;
    }

    public void enterEmail(String email) {
        enterValue(EMAIL_INPUT, email, "Email");

        element(EMAIL_NEXT_BUTTON).click();
        LOGGER.info("'Next' button clicked");
    }

    public void enterPassword(String password) {
        WebElement passwordInput = waiter().
                until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));

        passwordInput.sendKeys(password);
        LOGGER.info("{} entered as 'Password'", password);

        element(PASSWORD_NEXT_BUTTON).click();
        LOGGER.info("'Next' button clicked");

    }
}
