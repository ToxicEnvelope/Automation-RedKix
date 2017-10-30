package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WelcomeView extends RedkixAppView {

    private static final By WELCOME_VIEW_CONTAINER = By.cssSelector(".intro-page.welcome");
    private static final By ALREADY_HAVE_ACCOUNT_LINK = By.cssSelector(".already-have-account");

    private static final By WELCOME_VIEW_TITLE = By.cssSelector(".intro-page .title");
    private static final By WELCOME_VIEW_DESCRIPTION = By.cssSelector(".intro-page .description");


    public WelcomeView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return WELCOME_VIEW_CONTAINER;
    }

    public void clickAlreadyHaveAccount() {
        element(ALREADY_HAVE_ACCOUNT_LINK).click();
        LOGGER.info("'Already have an account?' link clicked");
    }

    public String getWelcomePageTitle() {
        return element(WELCOME_VIEW_TITLE).getText().trim();
    }

    public String getWelcomePageDescription() {
        return element(WELCOME_VIEW_DESCRIPTION).getText().trim();
    }

    public String getAlreadyHaveAccountLinkText() {
        return element(ALREADY_HAVE_ACCOUNT_LINK).getText();
    }
}
