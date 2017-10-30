package com.redkix.automation.views.main;

import com.redkix.automation.RedkixAppView;
import com.redkix.automation.model.Folder;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NavigationView extends RedkixAppView {

    public static final String UNREAD_CHANNELS = "Unread Channels";
    public static final String NOTIFICATIONS = "Notifications";
    public static final String EMAIL = "Email";


    private final By NAVIGATION_PANEL = By.cssSelector("[md-component-id=\"nav\"]");
    private final By FOLDERS_CONTAINER = By.cssSelector("[ng-model=\"mailBoxFolders\"]");
    private final By FOLDERS_LIST = By.cssSelector("[ng-include=\"'subFoldersPartial'\"] li");
    private final By EXPAND_FOLDERS_ICON = By.className("icon-rx-folder-state");
    private final By SETTINGS_BTN = By.cssSelector("[ng-click=\"settingsModal()\"]");
    private final By SPOTLIGHT_BTN = By.id("spotlight");
    private final By TOGGLE_FOLDERS_STATE_ICON = By.className("icon-rx-folder-state");

    private final By SEARCH_INPUT = By.cssSelector(".search-box input");

    private final By CONVERSATIONS_SECTION = By.id("pinned-conversations-section");
    private final By CHANNELS_SECTION = By.id("channels-section");
    private final By PEOPLE_SECTION = By.id("people-section");
    private final By BROWSER_CHANNELS_BUTTON = By.id("searchGroupButton");
    private final By CREATE_NEW_CHANNEL_BUTTON = By.id("createNewChannel");

    private final By TEXT_ITEM_IN_SIDEBAR = By.className("rx-side-bar-row-text");

    private final By TOOLTIP_CONTAINER = By.tagName("md-tooltip");


    public NavigationView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void waitTillReady() {
        waitForAngularRequestsToFinish();
        waitForMainLoaderToHide();
        waitForOverlayToHide();
        waiter().until(ready());
    }

    public void openSettingsView() {
        element(SETTINGS_BTN).click();
        LOGGER.info("'Settings' button clicked");
    }

    public void openFolders() {
        WebElement emailElement = getRowForRootFolder(EMAIL);
        emailElement.findElement(TOGGLE_FOLDERS_STATE_ICON).click();
        LOGGER.info("'>' for 'Email' clicked");
    }

    public void openFolder(Folder folder) {
        if (folder.getParent().isPresent()) {
            clickOnFolder(folder.getParent().get().getName());
        }

        clickOnFolder(folder.getName());
    }

    public void openFolder(String... path) {
        for (int i = 0; i < path.length; i++) {
            boolean last = i == path.length - 1;
            if (last) {
                clickOnFolder(path[i]);
            }
            else {
                expandFolderTree(path[i]);
            }
        }
    }

    private void expandFolderTree(String folderName) {
        WebElement folderElement = getFolderElement(folderName);
        folderElement.findElement(EXPAND_FOLDERS_ICON).click();
        LOGGER.info("'>' clicked to expand {} folder", folderName);
    }

    public void clickEmailLink() {
        WebElement emailElement = getRowForRootFolder(EMAIL).findElement(TEXT_ITEM_IN_SIDEBAR);
        emailElement.click();
        LOGGER.info("'{}' clicked", EMAIL);
    }

    public void openNotifications() {
        WebElement notifications = getRowForRootFolder(NOTIFICATIONS);
        notifications.click();
        LOGGER.info("'{}' clicked", NOTIFICATIONS);
    }

    private void clickOnFolder(String folderName) {
        WebElement element = getFolderElement(folderName);
        element.click();
        LOGGER.info("'{}' folder clicked", folderName);
    }

    private WebElement getFolderElement(String folderName) {
        return findAll(FOLDERS_LIST).stream().
                filter(WebElement::isDisplayed).
                filter(e -> e.getText().contains(folderName)).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Folder " + folderName + " is not found"));
    }

    public void openSpotlight() {
        element(SPOTLIGHT_BTN).click();
        LOGGER.info("'Spotlight' button clicked");
    }

    @Override
    protected By getViewIdentifier() {
        return SEARCH_INPUT;
    }

    public void hoverMouseOnSpotlightButton() {
        withAction().moveToElement(element(SPOTLIGHT_BTN)).build().perform();
    }

    public String getTooltipText() {
        return waiter().until(ExpectedConditions.visibilityOfElementLocated(TOOLTIP_CONTAINER)).getText();
    }

    public List<String> getConversations() {
        return getItemsFromSection(CONVERSATIONS_SECTION);
    }

    public List<String> getPeople() {
        List<String> people = getItemsFromSection(PEOPLE_SECTION);
        people.remove("PEOPLE");
        return people;
    }

    public List<String> getChannels() {
        List<String> channels = getItemsFromSection(CHANNELS_SECTION);
        channels.remove("CHANNELS");
        channels.remove("Create Channel");
        return channels;
    }

    public void selectConversation(String conversation) {
        WebElement conversationElement = element(CONVERSATIONS_SECTION).
                findElements(TEXT_ITEM_IN_SIDEBAR).stream().
                filter(WebElement::isDisplayed).
                filter(e -> conversation.equals(e.getText())).findFirst().
                orElseThrow(() -> new NoSuchElementException("Conversation " + conversation + " is not displayed"));

        conversationElement.click();
        LOGGER.info("'{}' clicked", conversation);
    }

    public void selectChannel(String channel) {
        WebElement conversationElement = element(CHANNELS_SECTION).
                findElements(TEXT_ITEM_IN_SIDEBAR).stream().
                filter(WebElement::isDisplayed).
                filter(e -> channel.equals(e.getText())).findFirst().
                orElseThrow(() -> new NoSuchElementException("Channel " + channel + " is not displayed"));

        conversationElement.click();
        LOGGER.info("'{}' clicked", channel);
    }

    public Optional<String> getSelectedConversation() {
        List<WebElement> selectedConversation = element(CONVERSATIONS_SECTION).
                findElements(By.cssSelector("li.selected")).stream().
                filter(WebElement::isDisplayed).collect(Collectors.toList());

        if (selectedConversation.size() == 1) {
            return Optional.of(selectedConversation.get(0).findElement(TEXT_ITEM_IN_SIDEBAR).getText());
        }
        else if (selectedConversation.size() > 1) {
            throw new IllegalStateException("More than one conversation selected(highlighted) at the same time");
        }

        return Optional.empty();
    }

    public Optional<String> getSelectedFolder() {
        List<WebElement> selectedConversation = element(FOLDERS_CONTAINER).
                findElements(By.cssSelector("li.selected")).stream().
                filter(WebElement::isDisplayed).collect(Collectors.toList());

        if (selectedConversation.size() == 1) {
            return Optional.of(selectedConversation.get(0).findElement(TEXT_ITEM_IN_SIDEBAR).getText());
        }
        else if (selectedConversation.size() > 1) {
            throw new IllegalStateException("More than one folder selected(highlighted) at the same time");
        }

        return Optional.empty();
    }

    public void clickBrowseChannelsButton() {
        element(BROWSER_CHANNELS_BUTTON).click();
        LOGGER.info("'Browse channels' button clicked");
    }

    public void clickCreateNewChannelButton() {
        element(CREATE_NEW_CHANNEL_BUTTON).click();
        LOGGER.info("'Create new channel' button clicked");
    }

    public String getSearchFieldValue() {
        return element(SEARCH_INPUT).getValue();
    }

    private List<String> getItemsFromSection(By sectionLocator) {
        try {
            return element(sectionLocator).findElements(TEXT_ITEM_IN_SIDEBAR).stream().
                    filter(WebElement::isDisplayed).
                    map(WebElement::getText).
                    collect(Collectors.toList());
        }
        catch (NoSuchElementException|StaleElementReferenceException e) {
            return Collections.emptyList();
        }
    }

    private WebElement getRowForRootFolder(String folderName) {
        return element(FOLDERS_CONTAINER).findElements(By.tagName("li")).stream().
                filter(e -> e.findElements(TEXT_ITEM_IN_SIDEBAR).stream().
                        map(WebElement::getText).anyMatch(t -> folderName.equals(t))).findFirst().
                orElseThrow(() -> new NoSuchElementException("'" + folderName + "' folder is not available"));
    }

}
