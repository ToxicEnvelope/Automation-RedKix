package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ChannelView extends RedkixAppView {

    public enum Tab {
        CHANNEL,
        THREADS,
        ARCHIVE
    }

    private static final By CHANNEL_HEADER = By.id("channelHeader");
    private static final By CHANNEL_TITLE = By.id("conversationSubjectHeader");
    private static final By CHANNEL_AVATAR = By.cssSelector("#channelHeader .avatar");
    private static final By CREATE_NEW_THREAD_BUTTON = By.cssSelector("[ng-click=\"openCreateNewThreadWidget(preview)\"] .text");
    private static final By NEW_THREAD_SUBJECT_INPUT = By.id("createNewThreadSubject");
    private static final By CREATE_BUTTON = By.cssSelector("[ng-click=\"createNewThread(preview);\"]");

    private static final By REPLY_ALL_INPUT = By.id("quick-reply");
    private static final By REPLY_ALL_BUTTON = By.id("replyAllSendButton");

    private static final By TAB_ELEMENT = By.className("tab");
    private static final By INVITE_OTHERS_LINK = By.className("invite-link");

    private MembersView membersView;

    public ChannelView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return CHANNEL_HEADER;
    }

    public void switchToTab(Tab tab) {
        findAll(TAB_ELEMENT).stream().filter(e -> tab.name().equalsIgnoreCase(e.getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Tab " + tab + " was not found")).
                click();
        LOGGER.info("{} tab clicked", tab);
    }

    public void createNewThread(String subject) {
        findAll(CREATE_NEW_THREAD_BUTTON).stream().filter(WebElement::isDisplayed).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("'Create new thread' button is not found")).
                click();
        LOGGER.info("'Create new thread' button clicked");

        enterValue(NEW_THREAD_SUBJECT_INPUT, subject, "New thread's subject");
        element(CREATE_BUTTON).click();
        LOGGER.info("'Create' button clicked");
    }

    public void replyAll(String text) {
        enterValue(REPLY_ALL_INPUT, text, "Reply all");

        element(REPLY_ALL_BUTTON).click();
        LOGGER.info("'Send' button clicked");
    }

    public void clickInviteOthersLink() {
        element(INVITE_OTHERS_LINK).click();
        LOGGER.info("'Invite others to this Channel' link clicked");
    }

    public void addMembersToChannel(String... emails) {
        membersView.addMembers(emails);
    }

    public void clickOnChannelTitle() {
        element(CHANNEL_TITLE).click();
        LOGGER.info("Channel title clicked");
    }

    public void clickOnChannelAvatar() {
        element(CHANNEL_AVATAR).click();
        LOGGER.info("Channel avatar clicked");
    }

    public String getChannelTitle() {
        return element(CHANNEL_TITLE).getText().trim();
    }
}
