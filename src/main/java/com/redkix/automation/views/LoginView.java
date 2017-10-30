package com.redkix.automation.views;


import com.redkix.automation.RedkixAppView;
import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@DefaultUrl("/")
public class LoginView extends RedkixAppView {

    private final By LOGIN_VIEW = By.id("rx-login-body");
    private final By WORK_EMAIL_FIELD = By.id("getStartedWorkEmail");
    private final By SIGN_IN_BTN = By.id("getStartedButton");
    private final By COPYRIGHT_TEXT_CONTAINER = By.className("more-info-container");
    private final By FULLY_SECURE_LINK = By.cssSelector(".more-info-container a");
    private final By LOGIN_VIEW_TITLE = By.cssSelector(".provider-discover-page .title");
    private final By LOGIN_VIEW_TEXT = By.cssSelector(".provider-discover-page .text");
    private final By WELCOME_EMAIL_LABEL = By.cssSelector(".onboard-label");

    public LoginView(WebDriver driver) {
        super(driver);
    }

    public void getStarted(String email) {
        enterEmail(email);

        element(SIGN_IN_BTN).click();
        LOGGER.info("'Sign In' button clicked");
    }

    public void enterEmail(String email) {
        enterValue(WORK_EMAIL_FIELD, email, "Email");
    }

    public String getLoginViewTitle() {
        return element(LOGIN_VIEW_TITLE).getText().trim();
    }

    public String getLoginViewText() {
        return element(LOGIN_VIEW_TEXT).getText().trim();
    }

    public String getCopyrightText() {
        return element(COPYRIGHT_TEXT_CONTAINER).getText();
    }

    public boolean isSignInButtonEnabled() {
        return element(SIGN_IN_BTN).isEnabled();
    }

    @Override
    protected By getViewIdentifier() {
        return LOGIN_VIEW;
    }

    public void clickOnFullySecureLink() {
        element(FULLY_SECURE_LINK).click();
    }

    public String getWelcomeEmailLabelText() {
        return element(WELCOME_EMAIL_LABEL).getText().trim();
    }
}
