package com.redkix.automation.compose_view;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;

import static com.redkix.automation.login.LoginScreenTests.SECURITY_PAGE_URL;
import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static com.redkix.automation.views.ComposeEmailView.ComposeEmailField.*;
import static com.redkix.automation.views.menu.TextEditorToolbar.EditorAction.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.openqa.selenium.Keys.ENTER;

@RunWith(SerenityRunner.class)
public class ComposeViewTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public ConversationSteps conversationSteps;



    @Test
    @Category(Sanity.class)
    public void CC24965_Recipients_Seperations_TO_CC_BCC() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);
        navigationSteps.clickEmailLink();

        navigationSteps.openComposeNewMessageView();

        String email1 = randomAlphabetic(5) + "@redkix.com";
        String email2 = randomAlphabetic(5) + "@redkix.com";

        createEmailSteps.enterValueInField(TO, email1, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(TO, email1);
        createEmailSteps.enterValueInField(TO, email2, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(TO, email1, email2);

        createEmailSteps.clickCcBccButton();

        createEmailSteps.enterValueInField(CC, email1, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(CC, email1);
        createEmailSteps.enterValueInField(CC, email2, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(CC, email1, email2);

        createEmailSteps.enterValueInField(BCC, email1, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(BCC, email1);
        createEmailSteps.enterValueInField(BCC, email2, ENTER);
        createEmailSteps.verifyThatEmailsInFieldMatches(BCC, email1, email2);
    }


    @Test
    @Category(Sanity.class)
    public void C219258_Body_Types() {
        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        loginSteps.loginToApp(user1);
        navigationSteps.clickEmailLink();

        //a few lines
        navigationSteps.openComposeNewMessageView();
        Email email1 = EmailHelper.createEmail(user2.getEmail());
        createEmailSteps.composeNewMessage(email1);

        //bold, italic, underline
        Email email2 = EmailHelper.createEmail(user2.getEmail()).setBody("");
        String[] words = {"test1", "test2", "test3"};
        navigationSteps.openComposeNewMessageView();
        createEmailSteps.fillEmailDetails(email2);
        createEmailSteps.addWord(words[0]);
        createEmailSteps.addWord(Keys.SPACE, Keys.LEFT);
        createEmailSteps.applyStyleToLine(words[0], BOLD);

        createEmailSteps.addWord(words[1]);
        createEmailSteps.addWord(Keys.SPACE, Keys.LEFT);
        createEmailSteps.applyStyleToLine(words[1], ITALIC);

        createEmailSteps.addWord(words[2]);
        createEmailSteps.addWord(Keys.SPACE, Keys.LEFT);
        createEmailSteps.applyStyleToLine(words[2], UNDERLINE);
        createEmailSteps.clickSendButton();

        //emoji
        Email email3 = EmailHelper.createEmail(user2.getEmail()).setBody("");
        navigationSteps.openComposeNewMessageView();
        createEmailSteps.fillEmailDetails(email3);
        String smileEmoji = "\uD83D\uDE04";
        createEmailSteps.addEmoji(smileEmoji);
        createEmailSteps.clickSendButton();

        //link
        Email email4 = EmailHelper.createEmail(user2.getEmail()).setBody("");
        navigationSteps.openComposeNewMessageView();
        createEmailSteps.fillEmailDetails(email4);
        createEmailSteps.addLink("Copyright", SECURITY_PAGE_URL);
        createEmailSteps.clickSendButton();

        navigationSteps.logOut();

        loginSteps.loginToApp(user2);
        navigationSteps.clickEmailLink();
        previewSteps.selectEmail(email1.getSubject());
        conversationSteps.verifyThatMessageBodyMatchesToExpected(email1.getBody());

        previewSteps.selectEmail(email2.getSubject());
        String expectedHtml = String.format("<p><b>%s</b>&nbsp;<i>%s</i>&nbsp;<u>%s</u>&nbsp;</p>", words[0], words[1],
                words[2]);
        conversationSteps.verifyThatMessageRawBodyMatchesToExpected(expectedHtml);

        previewSteps.selectEmail(email3.getSubject());
        conversationSteps.verifyThatMessageBodyMatchesToExpected(smileEmoji);

        previewSteps.selectEmail(email4.getSubject());

        expectedHtml = String.format("<p><a href=\"%s\" target=\"_blank\">%s</a></p>", SECURITY_PAGE_URL, "Copyright");
        conversationSteps.verifyThatMessageRawBodyMatchesToExpected(expectedHtml);
    }



}
