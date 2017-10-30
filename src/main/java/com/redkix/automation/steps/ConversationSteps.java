package com.redkix.automation.steps;


import com.redkix.automation.views.ContactCardView;
import com.redkix.automation.views.MessageDetailView;
import com.redkix.automation.views.main.ConversationView;
import net.thucydides.core.annotations.Step;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ConversationSteps {

    private ConversationView conversationView;
    private MessageDetailView messageDetailView;
    private ContactCardView contactCardView;

    @Step
    public void openMessageDetails() {
        conversationView.openMessageDetails();
    }

    @Step
    public void verifyValueInToFieldIsExpected(String email) {
        messageDetailView.openContactCard(MessageDetailView.CardFor.TO);
        String toEmail = contactCardView.getEmail();
        messageDetailView.closeContactCard();

        assertEquals("'To' value doesn't match", email, toEmail);
    }

    @Step
    public void verifyValueInCcFieldIsExpected(String email) {
        messageDetailView.openContactCard(MessageDetailView.CardFor.CC);
        String ccEmail = contactCardView.getEmail();
        messageDetailView.closeContactCard();

        assertEquals("'Cc' value doesn't match", email, ccEmail);
    }

    @Step
    public void verifyValueInBccFieldIsExpected(String email) {
        messageDetailView.openContactCard(MessageDetailView.CardFor.BCC);
        String ccEmail = contactCardView.getEmail();
        messageDetailView.closeContactCard();

        assertEquals("'Bcc' value doesn't match", email, ccEmail);
    }

    @Step
    public void verifyCcFieldIsNotAvailable() {
        assertFalse("'Bcc' field is present",
                messageDetailView.isSectionPresent(MessageDetailView.CardFor.BCC));
    }

    @Step
    public void verifyBccFieldIsNotAvailable() {
        assertFalse("'Bcc' field is present",
                messageDetailView.isSectionPresent(MessageDetailView.CardFor.BCC));
    }

    @Step
    public void verifyThatMessageBodyMatchesToExpected(String expectedBody) {
        assertEquals(expectedBody, conversationView.getMessageBody().trim());
    }

    @Step
    public void verifyThatMessageRawBodyMatchesToExpected(String expectedRawHtml) {
        assertEquals(expectedRawHtml, conversationView.getMessageBodyAsRawHtml());
    }

    @Step
    public void moveToArchive() {
        conversationView.clickActionButton(ConversationView.ActionButton.ARCHIVE);
    }

    @Step
    public void moveToTrash() {
        conversationView.clickActionButton(ConversationView.ActionButton.TRASH);
    }

    @Step
    public void moveToDestinationFolder(String path) {
        conversationView.moveToFolder(path.split("->"));
    }
}
