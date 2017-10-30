package com.redkix.automation.conversation_list;

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
public class ConversationTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public EmailServiceSteps emailServiceSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public ConversationSteps conversationSteps;

    @Test
    @Category(Sanity.class)
    public void C219662_Conversations_Only_one_Conversation_is_highlighted_at_a_time() {
        User user1 = new User("qatest4@redkix.com", "Redkix999").setServiceType(EXCHANGE);
        User user2 = new User("qatest6@redkix.com", "Redkix111").setServiceType(EXCHANGE);

        Email conversation1 = EmailHelper.createEmail(user2.getEmail()).setBody(randomAlphabetic(5));
        emailServiceSteps.sendEmail(user1, conversation1);
        emailServiceSteps.replyToEmail(user2, conversation1, randomAlphabetic(5));

        Email conversation2 = EmailHelper.createEmail(user1.getEmail()).setBody(randomAlphabetic(5));
        emailServiceSteps.sendEmail(user2, conversation2);
        emailServiceSteps.replyToEmail(user1, conversation2, randomAlphabetic(5));

        loginSteps.loginToApp(user2);
        navigationSteps.clickEmailLink();

        previewSteps.selectEmail(conversation1.getSubject());
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversation1.getSubject());

        previewSteps.minimizeBrowser();
        previewSteps.maximizeBrowser();
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversation1.getSubject());

        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.SENT_EMAIL);
        previewSteps.selectEmail(conversation1.getSubject());
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversation1.getSubject());

//        previewSteps.reloadApplication();
//        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversation1.getSubject());
    }

    @Test
    @Category(Sanity.class)
    public void C219666_Conversations_Move_animation_Archive__Delete__Move_by_Action_buttons() {
        User user1 = UserHelper.getUserByEmail("qatest4@redkix.com");
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        Email[] conversations = new Email[5];
        for (int i = 0; i < conversations.length; i++) {
            conversations[i] = EmailHelper.createEmail(user1.getEmail()).setBody(randomAlphabetic(5));
            emailServiceSteps.sendEmail(user2, conversations[i]);
            emailServiceSteps.replyToEmail(user1, conversations[i], randomAlphabetic(5));
        }

        loginSteps.loginToApp(user1);
        //previewSteps.clickNotNowInTheEnableDesktopNotificationsMessage();

        navigationSteps.clickEmailLink();

        //move to Archive
        navigationSteps.clickEmailLink();
        previewSteps.selectEmail(conversations[4].getSubject());
        conversationSteps.moveToArchive();
        previewSteps.verifyThatConversationIsNotDisplayed(conversations[4]);
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversations[3].getSubject());
        navigationSteps.openFolders();
        navigationSteps.openFolder(Folder.ARCHIVE);
        previewSteps.verifyThatConversationIsDisplayed(conversations[4]);

        //move to Trash
        navigationSteps.clickEmailLink();
        previewSteps.selectEmail(conversations[3].getSubject());
        conversationSteps.moveToTrash();
        previewSteps.verifyThatConversationIsNotDisplayed(conversations[3]);
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversations[2].getSubject());
        navigationSteps.openFolder(Folder.DELETED_ITEMS);
        previewSteps.verifyThatConversationIsDisplayed(conversations[3]);

        //move to Junk
        navigationSteps.clickEmailLink();
        previewSteps.selectEmail(conversations[2].getSubject());
        conversationSteps.moveToDestinationFolder(Folder.JUNK_EMAIL.getName());
        previewSteps.verifyThatConversationIsNotDisplayed(conversations[2]);
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversations[1].getSubject());
        navigationSteps.openFolder(Folder.JUNK_EMAIL);
        previewSteps.verifyThatConversationIsDisplayed(conversations[2]);

        //move to Archive - Bypassed groups
        navigationSteps.clickEmailLink();
        previewSteps.selectEmail(conversations[1].getSubject());
        String path = "Archive->Bypassed groups";
        conversationSteps.moveToDestinationFolder(path);
        previewSteps.verifyThatConversationIsNotDisplayed(conversations[1]);
        previewSteps.verifyThatSubjectOfHighlightedEmailMatchesTo(conversations[0].getSubject());
        navigationSteps.openFolder(path);
        previewSteps.verifyThatConversationIsDisplayed(conversations[1]);
    }

    @Test
    @Category(Sanity.class)
    public void CXXXX_Search_for_email() {
        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        Email email = EmailHelper.createEmail(user1.getEmail());
        emailServiceSteps.sendEmail(user2, email);

        loginSteps.loginToApp(user1);

        navigationSteps.clickEmailLink();
        previewSteps.waitAndVerifyThatConversationIsDisplayed(email);
        previewSteps.waitABit(10_000);
        previewSteps.search(email.getSubject());
        previewSteps.verifyThatConversationIsDisplayed(email);
    }
}
