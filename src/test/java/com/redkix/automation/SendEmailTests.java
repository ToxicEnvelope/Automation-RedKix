package com.redkix.automation;


import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.Folder;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
@Ignore
public class SendEmailTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public ConversationSteps conversationSteps;


    @Test
    public void Compose_a_new_message_with_1_TO_participant_and_1_CC_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");
        User user3 = new User("qatest7@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail(user2.getEmail());
        email.addCc(user3.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        openEmailDetailsAndCheckToAndCcValues(user2, user3, email);

        User[] receivers = {user2, user3};

        for (User receiver : receivers) {
            navigationSteps.logOut();
            loginSteps.loginToApp(receiver);
            openEmailDetailsAndCheckToAndCcValues(user2, user3, email);
        }
    }


    @Test
    public void Compose_a_new_message_with_1_TO_participant_and_1_BCC_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");
        User user3 = new User("qatest7@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail(user2.getEmail());
        email.addBcc(user3.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInToFieldIsExpected(user2.getEmail());
        conversationSteps.verifyValueInBccFieldIsExpected(user3.getEmail());

        User[] receivers = {user2, user3};

        for (User receiver : receivers) {
            navigationSteps.logOut();

            loginSteps.loginToApp(receiver);

            previewSteps.selectEmail(email.getSubject());
            conversationSteps.openMessageDetails();
            conversationSteps.verifyValueInToFieldIsExpected(user2.getEmail());

            conversationSteps.verifyCcFieldIsNotAvailable();
            conversationSteps.verifyBccFieldIsNotAvailable();
        }
    }


    @Test
    public void Compose_a_new_message_with_1_CC_participant_and_1_BCC_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");
        User user3 = new User("qatest7@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail();
        email.addCc(user2.getEmail());
        email.addBcc(user3.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInCcFieldIsExpected(user2.getEmail());
        conversationSteps.verifyValueInBccFieldIsExpected(user3.getEmail());

        User[] receivers = {user2, user3};

        for (User receiver : receivers) {
            navigationSteps.logOut();

            loginSteps.loginToApp(receiver);

            previewSteps.selectEmail(email.getSubject());
            conversationSteps.openMessageDetails();
            conversationSteps.verifyValueInCcFieldIsExpected(user2.getEmail());
            conversationSteps.verifyBccFieldIsNotAvailable();
        }
    }

    @Test
    public void Compose_a_new_message_with_1_CC_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail();
        email.addCc(user2.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInCcFieldIsExpected(user2.getEmail());

        navigationSteps.logOut();

        loginSteps.loginToApp(user2);

        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInCcFieldIsExpected(user1.getEmail());
    }

    @Test
    public void Compose_a_new_message_with_1_TO_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail(user2.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInToFieldIsExpected(user2.getEmail());

        navigationSteps.logOut();

        loginSteps.loginToApp(user2);

        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInToFieldIsExpected(user2.getEmail());
    }

    @Test
    public void Compose_a_new_message_with_1_BCC_participant() {

        User user1 = new User("qatest4@redkix.com", "Redkix999");
        User user2 = new User("qatest6@redkix.com", "Redkix111");

        loginSteps.loginToApp(user1);

        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail();
        email.addBcc(user2.getEmail());

        createEmailSteps.composeNewMessage(email);

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInBccFieldIsExpected(user2.getEmail());

        navigationSteps.logOut();

        loginSteps.loginToApp(user2);

        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyBccFieldIsNotAvailable();
    }


    private void openEmailDetailsAndCheckToAndCcValues(User toUser, User ccValue, Email email) {
        previewSteps.selectEmail(email.getSubject());
        conversationSteps.openMessageDetails();
        conversationSteps.verifyValueInToFieldIsExpected(toUser.getEmail());
        conversationSteps.verifyValueInCcFieldIsExpected(ccValue.getEmail());
    }


}
