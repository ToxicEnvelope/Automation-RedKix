package com.redkix.automation.steps;


import com.redkix.automation.model.Email;
import com.redkix.automation.views.ComposeEmailView;
import com.redkix.automation.views.menu.TextEditorToolbar.EditorAction;
import net.thucydides.core.annotations.Step;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.redkix.automation.views.menu.TextEditorToolbar.EditorAction.LINK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateEmailSteps extends RedkixScenarioSteps {

    private ComposeEmailView composeEmailView;

    @Step
    public void composeNewMessage(Email email) {
        composeEmailView.sendNewEmail(email);
    }

    @Step
    public void clickCcBccButton() {
        composeEmailView.clickCcBccButton();
    }

    @Step
    public void checkThatTypingCursorSetForToField() {
        assertTrue("Typing cursor is not set for 'To' field", composeEmailView.isToFieldFocused());
    }

    @Step
    public void pressEsc() {
        composeEmailView.pressEsc();
    }

    @Step
    public void checkThatComposeEmailViewClosed() {
        assertTrue("'Compose' is not closed", composeEmailView.isHidden());
    }

    @Step("Enter value {1} in {0} field")
    public void enterValueInField(ComposeEmailView.ComposeEmailField field, CharSequence... value) {
        composeEmailView.enterValueInField(field, value);
    }

    @Step("Clear {0} field")
    public void clearToField(ComposeEmailView.ComposeEmailField field) {
        composeEmailView.clearField(field);
    }

    @Step("Verify that emails in {0} field matches to expected: {1}")
    public void verifyThatEmailsInFieldMatches(ComposeEmailView.ComposeEmailField field, String... emails) {
        assertEquals(Arrays.asList(emails), composeEmailView.getEmailsFromField(field));
    }

    @Step
    public void fillEmailDetails(Email email) {
        composeEmailView.fillEmailDetails(email);;
    }

    @Step
    public void clickSendButton() {
        composeEmailView.clickSendButton();
    }

    @Step("Select {1} in editor for {0}")
    public void applyStyleToLine(String word, EditorAction action) {
        composeEmailView.applyExtraToLastWord(word, action);
    }

    @Step
    public void addEmoji(String emoji) {
        composeEmailView.addEmoji(emoji);
    }

    @Step
    public void addAttachment(File file) {
        composeEmailView.addFile(file);
        waitForResult(f -> composeEmailView.getAttachments().contains(f), file.getName());
    }

    @Step
    public void addLink(String link, String href) {
        composeEmailView.sendKeysToBody(link);
        composeEmailView.applyExtraToLastWord(link, LINK, href);
    }

    @Step
    public void addWord(CharSequence... keys) {
        composeEmailView.sendKeysToBody(keys);
    }

    @Step
    public void removeAttachment(String name) {
        composeEmailView.removeAttachment(name);
    }

    @Step
    public void verifyThatListOfAttachmentsMatchesTo(List<String> attachments) {
        assertEquals(attachments, composeEmailView.getAttachments());
    }


}
