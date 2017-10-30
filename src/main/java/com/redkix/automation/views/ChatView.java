package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ChatView extends RedkixAppView {

    private static final By CONVERSATION_SUBJECT_CONTAINER = By.id("conversationSubjectHeader");

    public ChatView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return CONVERSATION_SUBJECT_CONTAINER;
    }

    public String getConversationSubject() {
        return element(CONVERSATION_SUBJECT_CONTAINER).getText().trim();
    }
}
