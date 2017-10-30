package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class BrowseChannelsListView extends RedkixAppView {

    private static final By BROWSER_CHANNELS_CONTAINER = By.className("header-previews-and-conversation");
    private static final By SEARCH_INPUT = By.cssSelector("[name=\"searchGroup\"] .user-input");
    private static final By CLEAR_SEARCH_BUTTON = By.className("clear-search");
    private static final By EMPTY_SEARCH_RESULT_CONTAINER = By.className("search-groups-empty-view");
    private static final By CHANNEL_TITLE = By.cssSelector(".group-details .title");
    private static final By CHANNEL_SUB_TITLE = By.cssSelector(".group-details .sub-title");

    private static final By CREATE_NEW_CHANNEL_BUTTON = By.className("create-new-channel");

    private static final By PRIVATE_CHANNEL_SWITCHER = By.cssSelector("[icon-off=\"icon-lock\"]");
    private static final By NEW_CHANNEL_NAME_INPUT = By.cssSelector("[name=\"groupNameForm\"] [name=\"groupName\"]");
    private static final By INVITE_RECIPIENTS_AREA = By.cssSelector(".add-members-container .add-members input");
    private static final By CREATE_CHANNEL_BUTTON = By.className("create-channel-button");

    public BrowseChannelsListView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return BROWSER_CHANNELS_CONTAINER;
    }

    public void clearSearch() {
        element(CLEAR_SEARCH_BUTTON).click();
        LOGGER.info("'x' in search field clicked");
    }

    public void search(String searchTerm) {
        enterValue(SEARCH_INPUT, searchTerm, "Search term");
    }

    public List<String> getDisplayedChannelTitles() {
        waitForAjaxRequestsToFinish();

        waiter().until(ExpectedConditions.visibilityOfElementLocated(CHANNEL_TITLE));

        return findAll(CHANNEL_TITLE).stream().
                filter(WebElement::isDisplayed).
                map(WebElement::getText).
                collect(Collectors.toList());
    }

    public List<String> getEmptySearchResultsMessage() {
        return waiter().
                until(ExpectedConditions.visibilityOfElementLocated(EMPTY_SEARCH_RESULT_CONTAINER)).
                findElements(By.tagName("span")).stream().
                map(WebElement::getText).
                collect(Collectors.toList());
    }

    public void createNewChannel(String name, boolean isPrivate, String... invitees) {
        element(CREATE_NEW_CHANNEL_BUTTON).click();
        LOGGER.info("'Create new channel' button clicked");

        if (isPrivate != isNewChannelPrivate()) {
            element(PRIVATE_CHANNEL_SWITCHER).click();
            LOGGER.info("Switched channel to be " + (isPrivate ? "private": "public"));
        }

        enterValue(NEW_CHANNEL_NAME_INPUT, name, "Channel name");

        if (invitees != null) {
            for (String invitee : invitees) {
                element(INVITE_RECIPIENTS_AREA).sendKeys(invitee, Keys.ENTER);
                LOGGER.info("'{}' added as invitee", invitee);
            }
        }

        element(CREATE_CHANNEL_BUTTON).click();
        LOGGER.info("'Create channel' button clicked");
    }

    public boolean isNewChannelPrivate() {
        return "false".equals(element(PRIVATE_CHANNEL_SWITCHER).
                findElement(By.tagName("input")).
                getAttribute("aria-checked"));
    }


}
