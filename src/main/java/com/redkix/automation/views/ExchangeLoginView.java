package com.redkix.automation.views;

import com.google.common.base.Objects;
import com.redkix.automation.RedkixAppView;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ExchangeLoginView extends RedkixAppView {

    private final By EXCHANGE_LOGIN_VIEW = By.cssSelector("[ng-switch-when=\"exchange\"]");

    private final By WORK_EMAIL_FIELD = By.name("email");
    private final By PASSWORD_FIELD = By.name("password");
    private final By SIGN_IN_BTN = By.name("signin");


    public ExchangeLoginView(WebDriver driver) {
        super(driver);
    }

    public void signIn(String email, String password) {

        WebElementFacade emailField = element(WORK_EMAIL_FIELD);

        if (Objects.equal(emailField.getValue(), email)) {
            LOGGER.info("{} is already entered as email", email);
        }
        else {
            emailField.clear();
            emailField.sendKeys(email);
            LOGGER.info("{} entered as email", email);
        }

        element(PASSWORD_FIELD).sendKeys(password);
        LOGGER.info("{} entered as password", password);

        element(SIGN_IN_BTN).click();
        LOGGER.info("'Sign In' button clicked");
    }

    @Override
    protected By getViewIdentifier() {
        return EXCHANGE_LOGIN_VIEW;
    }
}
