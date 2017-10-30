package com.redkix.automation.steps;

import com.redkix.automation.model.User;
import com.redkix.automation.views.ExchangeLoginView;
import com.redkix.automation.views.GMailSignInView;
import com.redkix.automation.views.LoginView;
import com.redkix.automation.views.WelcomeView;
import com.redkix.automation.views.main.NavigationView;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static com.redkix.automation.model.EmailServiceType.GMAIL;
import static org.junit.Assert.*;


public class LoginSteps extends RedkixScenarioSteps {

    private WelcomeView welcomeView;
    private LoginView loginView;
    private ExchangeLoginView exchangeLoginView;
    private NavigationView navigationView;
    private GMailSignInView gMailSignInView;


    @Steps
    private NavigationSteps navigationSteps;

    @Step
    public void submitEmailAndClickGetStarted(String email) {
        loginView.getStarted(email);
    }

    @Step
    public void enterEmail(String email) {
        loginView.enterEmail(email);
    }

    @Step
    public void signIn(String email, String password) {
        exchangeLoginView.signIn(email, password);
    }

    @Step
    public void loginToApp(User user) {
        openLoginView();

        submitEmailAndClickGetStarted(user.getEmail());

        if (user.getServiceType() == EXCHANGE) {
            signIn(user.getEmail(), user.getPassword());
        }
        else if (user.getServiceType() == GMAIL) {
            String currentWindowHandle = getDriver().getWindowHandle();

            String googleSignInWindow = waitForNewWindowHandle();
            getDriver().switchTo().window(googleSignInWindow);
            gMailSignInView.enterEmail(user.getEmail());
            gMailSignInView.enterPassword(user.getPassword());
            boolean isOneHandlePresent = waitForOneWindowHandle();

            if (! isOneHandlePresent) {
                throw new IllegalArgumentException("GMail sign in window is not closed - something is wrong");
            }

            getDriver().switchTo().window(currentWindowHandle);
        }
        else {
            throw new IllegalArgumentException("Service type should be defined for user in order to login!");
        }
    }

    @Step
    public void openLoginView() {
        openWelcomeView();
        clickAlreadyHaveAccountLink();
    }

    @Step
    public void openWelcomeView() {
        welcomeView.open();
    }

    @Step
    public void clickAlreadyHaveAccountLink() {
        welcomeView.clickAlreadyHaveAccount();
    }

    @Step
    public void checkThatGetStartedButtonIsDisabled() {
        assertFalse("'Get started' button enabled", loginView.isSignInButtonEnabled());
    }

    @Step
    public void checkThatGetStartedButtonIsEnabled() {
        assertTrue("'Get started' button disabled", loginView.isSignInButtonEnabled());
    }

    @Step
    public void checkThatCopyrightMatchesTo(String expectedText) {
        assertEquals(expectedText, loginView.getCopyrightText());
    }

    @Step
    public void clickOnFullySecure() {
        loginView.clickOnFullySecureLink();
    }

    @Step
    public void checkThatUrlOfOpenedPageMatchesToExpected(String expected) {
        String securePageWindow = waitForNewWindowHandle();
        getDriver().switchTo().window(securePageWindow);

        assertEquals(expected, getDriver().getCurrentUrl());
    }

    @Step
    public void verifyThatWelcomePageTitleMatchesTo(String expectedTitle) {
        assertEquals(expectedTitle, welcomeView.getWelcomePageTitle());
    }

    @Step
    public void verifyThatWelcomePageDescriptionMatchesTo(String expectedDescription) {
        assertEquals(expectedDescription, welcomeView.getWelcomePageDescription());
    }

    @Step
    public void verifyThatAlreadyHaveAccountLinkTextMatchesTo(String expectedText) {
        assertEquals(expectedText, welcomeView.getAlreadyHaveAccountLinkText());
    }

    @Step
    public void checkThatLoginPageTitleMatchesTo(String expectedText) {
        assertEquals(expectedText, loginView.getLoginViewTitle());
    }

    @Step
    public void checkThatLoginPageTextMatchesTo(String expectedText) {
        assertEquals(expectedText, loginView.getLoginViewText());
    }

    @Step
    public void checkThatWorkEmailLabelMatchesTo(String expected) {
        assertEquals(expected, loginView.getWelcomeEmailLabelText());
    }

    private boolean waitForOneWindowHandle() {
        return waitForResult(driver -> driver.getWindowHandles().size() == 1, getDriver());
    }


    private String waitForNewWindowHandle() {
        String currentHandle = getDriver().getWindowHandle();

        waitForResult(driver -> driver.getWindowHandles().stream().anyMatch(h -> !h.equals(currentHandle)),
                getDriver());

        return getDriver().getWindowHandles().stream().
                filter(h -> !h.equals(currentHandle)).
                findFirst().
                get();
    }



}
