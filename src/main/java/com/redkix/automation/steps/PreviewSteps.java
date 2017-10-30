package com.redkix.automation.steps;

import com.redkix.automation.model.Email;
import com.redkix.automation.views.main.PreviewView;
import net.thucydides.core.annotations.Step;

import java.util.Arrays;
import java.util.List;

import static com.redkix.automation.model.Folder.ARCHIVE;
import static com.redkix.automation.model.Folder.DELETED_ITEMS;
import static org.junit.Assert.*;


public class PreviewSteps extends RedkixScenarioSteps {

    private PreviewView previewView;

    @Step
    public void selectEmail(String subject) {
        previewView.selectEmailBySubject(subject);
    }

    @Step
    public List<String> getDisplayedSubjects() {
        return previewView.getDisplayedSubjects();
    }

    @Step
    public void verifyThatEmailIsMarkedAsUnread(Email email) {
        boolean isUnreadRead = waitForResult(e -> !previewView.isRead(e), email);
        assertTrue("Email is not marked as unread", isUnreadRead);
    }

    @Step
    public void verifyThatEmailIsMarkedAsRead(Email email) {
        boolean isRead = waitForResult(e -> previewView.isRead(e), email);
        assertTrue("Email is not marked as read", isRead);
    }

    @Step
    public void markEmailAsUnread(Email email) {
        previewView.setIsRead(email, false);
    }

    @Step
    public void markEmailAsRead(Email email) {
        previewView.setIsRead(email, true);
    }

    @Step
    public void verifyThatSubjectOfHighlightedEmailMatchesTo(String subject) {
        String actualSubject = previewView.getSelectedEmailSubject().
                orElseThrow(() -> new AssertionError("No email is selected"));

        assertEquals(subject, actualSubject);
    }

    @Step
    public void moveConversationToArchiveViaContextMenu(Email email) {
        previewView.moveConversationTo(email, ARCHIVE.getName());
    }

    @Step
    public void moveConversationToDeleteViaContextMenu(Email conversation) {
        previewView.moveConversationTo(conversation, DELETED_ITEMS.getName());
    }

    @Step
    public void verifyThatConversationIsNotDisplayed(Email conversation) {
        assertFalse("Conversation  " + conversation.getSubject() + " is still displayed",
                previewView.getDisplayedSubjects().contains(conversation.getSubject()));
    }

    @Step
    public void verifyThatConversationIsDisplayed(Email conversation) {
        assertTrue("Conversation " + conversation.getSubject() + " is not displayed",
                previewView.getDisplayedSubjects().contains(conversation.getSubject()));
    }

    @Step
    public void waitAndVerifyThatConversationIsDisplayed(Email conversation) {
        assertTrue("Conversation " + conversation.getSubject() + " is not displayed",
                waitForResult(v -> v.getDisplayedSubjects().contains(conversation.getSubject()), previewView));
    }

    @Step
    public void search(String searchTerm) {
        previewView.searchAll(searchTerm);
    }

    @Step
    public void clearSearchField() {
        previewView.clearSearchField();
    }

    @Step
    public void verifyThatDisplayedSubjectsMatches(List<String> expected) {
        assertEquals(expected, previewView.getDisplayedSubjects());
    }

    @Step
    public void checkThatNoResultsMessageInPreviewMatchesTo(String... expected) {
        assertArrayEquals(expected, previewView.getEmptyResultsDetailedMessage());
    }

    @Step
    public void checkThatDisplayedTabsMatchTo(PreviewView.PreviewTab... tabs) {
        List<PreviewView.PreviewTab> expected = Arrays.asList(tabs);

        assertEquals(expected, previewView.getDisplayedTabs());
    }

}
