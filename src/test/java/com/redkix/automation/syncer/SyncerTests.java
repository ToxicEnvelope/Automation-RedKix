package com.redkix.automation.syncer;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.Folder;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@RunWith(SerenityRunner.class)
public class SyncerTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public EmailServiceSteps emailServiceSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public ConversationSteps conversationSteps;

    @Test
    @Category(Sanity.class)
    public void CXXXXX_Test_external_user_receives_message() {
        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getExternalUser();

        loginSteps.loginToApp(user1);
        Email email = EmailHelper.createEmail(user2.getEmail());

        navigationSteps.openComposeNewMessageView();
        createEmailSteps.composeNewMessage(email);

        emailServiceSteps.checkThatEmailExists(user2, email);
    }

    @Test
    @Category(Sanity.class)
    public void CXXXXX_Test_sent_email_in_native_same_as_in_redkix() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        Email email = EmailHelper.createEmail(UserHelper.getUser(EXCHANGE, user).getEmail());

        navigationSteps.openComposeNewMessageView();
        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(email.getSubject());

        emailServiceSteps.checkThatEmailExists(user, email, Folder.SENT_EMAIL);
    }

    @Test
    @Category(Sanity.class)
    public void CXXXXX_Send_message_from_external_user_to_logged_in_user() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);
        navigationSteps.clickEmailLink();

        User externalUser = UserHelper.getExternalUser();
        Email email = EmailHelper.createEmail(user.getEmail()).setBody(randomAlphabetic(5));
        emailServiceSteps.sendEmail(externalUser, email);

        previewSteps.waitAndVerifyThatConversationIsDisplayed(email);

        previewSteps.selectEmail(email.getSubject());
        conversationSteps.verifyThatMessageBodyMatchesToExpected(email.getBody());
    }
}
