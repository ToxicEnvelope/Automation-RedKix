package com.redkix.automation.initializer;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.EmailServiceType;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.EmailServiceSteps;
import com.redkix.automation.steps.LoginSteps;
import com.redkix.automation.steps.NavigationSteps;
import com.redkix.automation.steps.PreviewSteps;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;

@RunWith(SerenityRunner.class)
public class InitializerTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public EmailServiceSteps emailServiceSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Test
    @Category(Sanity.class)
    public void C219112_Read_unread() {
        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        Email email = EmailHelper.createEmail(user2.getEmail());
        emailServiceSteps.sendEmail(user1, email);

        loginSteps.loginToApp(user2);

        navigationSteps.clickEmailLink();

        emailServiceSteps.markEmailAsReadViaService(user2, email);
        previewSteps.verifyThatEmailIsMarkedAsRead(email);

        emailServiceSteps.markEmailAsUnreadViaService(user2, email);
        previewSteps.verifyThatEmailIsMarkedAsUnread(email);

        previewSteps.markEmailAsRead(email);
        emailServiceSteps.checkEmailStateCorrespondsToExpected(user2, email, true);

        previewSteps.markEmailAsUnread(email);
        emailServiceSteps.checkEmailStateCorrespondsToExpected(user2, email, false);
    }
}
