package com.redkix.automation.login;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.steps.LoginSteps;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class LoginScreenTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    public static final String WELCOME_TO_REDKIX_TEXT = "Welcome To Redkix";
    public static final String WELCOME_PAGE_DESCRIPTION = "Redkix is team messaging powered by email. That's right, " +
                                                          "the best of team messaging with the ability to include " +
                                                          "anyone in the conversation.";
    public static final String ALREADY_HAVE_AN_ACCOUNT_LINK_TEXT = "ALREADY HAVE AN ACCOUNT?";
    public static final String WELCOME_BACK_TEXT = "Welcome back!";
    public static final String SIGN_IN_WITH_EMAIL_YOU_USED_TO_CREATE_ACCOUNT_TEXT = "Sign in with the email address " +
                                                                                    "you used to create your Redkix account.";


    public static final String SECURITY_PAGE_URL = "https://redkix.com/security/";
    public static final String COPYRIGHT_TEXT = "\u00A9 Redkix, Inc. is fully secure";
    public static final String WORK_EMAIL = "Work email";

    @Test
    @Category(Sanity.class)
    public void CC219057_Login_screen_GUI_validation() {
        loginSteps.openWelcomeView();

        loginSteps.verifyThatWelcomePageTitleMatchesTo(WELCOME_TO_REDKIX_TEXT);
        loginSteps.verifyThatWelcomePageDescriptionMatchesTo(WELCOME_PAGE_DESCRIPTION);
        loginSteps.verifyThatAlreadyHaveAccountLinkTextMatchesTo(ALREADY_HAVE_AN_ACCOUNT_LINK_TEXT);

        loginSteps.clickAlreadyHaveAccountLink();

        loginSteps.checkThatLoginPageTitleMatchesTo(WELCOME_BACK_TEXT);
        loginSteps.checkThatLoginPageTextMatchesTo(SIGN_IN_WITH_EMAIL_YOU_USED_TO_CREATE_ACCOUNT_TEXT);

        loginSteps.checkThatWorkEmailLabelMatchesTo(WORK_EMAIL);

        loginSteps.checkThatGetStartedButtonIsDisabled();

        loginSteps.checkThatCopyrightMatchesTo(COPYRIGHT_TEXT);

        loginSteps.clickOnFullySecure();

        loginSteps.checkThatUrlOfOpenedPageMatchesToExpected(SECURITY_PAGE_URL);
    }


    @Test
    @Category(Sanity.class)
    public void C219059_Login_screen_Get_started_button() {
        loginSteps.openLoginView();
        loginSteps.checkThatGetStartedButtonIsDisabled();

        String[] badEmails = {"@.com", "test@.c", "#kkd@google.com"};

        for (String badEmail : badEmails) {
            loginSteps.enterEmail(badEmail);
            loginSteps.checkThatGetStartedButtonIsDisabled();
        }

        loginSteps.enterEmail(UserHelper.getUser().getEmail());
        loginSteps.checkThatGetStartedButtonIsEnabled();

        loginSteps.enterEmail(badEmails[0]);
        loginSteps.checkThatGetStartedButtonIsDisabled();
    }
}
