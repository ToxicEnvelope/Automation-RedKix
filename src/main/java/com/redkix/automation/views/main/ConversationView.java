package com.redkix.automation.views.main;


import com.redkix.automation.RedkixAppView;
import com.redkix.automation.model.Folder;
import com.redkix.automation.views.menu.MoveConversationMenu;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;

import static com.redkix.automation.views.main.ConversationView.ActionButton.MOVE;

public class ConversationView extends RedkixAppView {

    public enum ActionButton {
        FAVORITE,
        MARK_LAST_MESSAGE_UNREAD,
        ARCHIVE,
        TRASH,
        MOVE,
        SCHEDULE
    }

    private static final By CONVERSATION_PANEL = By.id("conversationPane");
    private static final By SINGLE_MESSAGE_BLOCK = By.className("rxxx-message-single");
    private static final By REACTION_AREA = By.className("message-and-reaction");
    private static final By MORE_BTN = By.cssSelector("#conversationPane [aria-label=\"More\"]");
    private static final By SHOW_HIDE_DETAILS_BTN = By.cssSelector("[ng-click=\"messageDetails.show = !messageDetails.show\"]");
    private static final By MESSAGE_BODY = By.cssSelector("#conversationPane .rx-message-body");
    private static final By CONVERSATION_HEADER = By.id("conversationHeader");


    /** Buttons **/
    private static final By FAVORITE_BUTTON = By.id("favButton");
    private static final By MARK_LAST_MESSAGE_UNREAD_BUTTON = By.id("markLastMessageAsUnreadButton");
    private static final By ARCHIVE_BUTTON = By.id("archiveButton");
    private static final By TRASH_BUTTON = By.id("trashButton");
    private static final By MOVE_BUTTON = By.id("moveButton");
    private static final By SCHEDULE_BUTTON = By.id("scheduleBtnInMessagesView");

    private final String MESSAGE_DETAILS = "Message Details";

    private MoveConversationMenu moveConversationMenu;

    public ConversationView(WebDriver driver) {
        super(driver);
    }

    public void openMessageDetails() {
        withAction().moveToElement(element(SINGLE_MESSAGE_BLOCK)).click().build().perform();
        //element(REACTION_AREA).click();

        findAll(MORE_BTN).stream().filter(WebElement::isDisplayed).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("'More' button is not available")).
                click();
        LOGGER.info("'More' button clicked");

        WebElementFacade showHideBtn = element(SHOW_HIDE_DETAILS_BTN);

        if (showHideBtn.getText().contains(MESSAGE_DETAILS)) {
            showHideBtn.click();
            LOGGER.info("'{}' clicked", MESSAGE_DETAILS);
        }
    }

    public String getMessageBody() {
        return getMessageBodyElement().getText();
    }

    public String getMessageBodyAsRawHtml() {
        return getMessageBodyElement().findElement(By.className("ng-scope")).
                getAttribute("innerHTML");
    }

    private WebElement getMessageBodyElement() {
        return waiter().until(ExpectedConditions.visibilityOf(element(MESSAGE_BODY)));
    }

    @Override
    protected By getViewIdentifier() {
        return CONVERSATION_PANEL;
    }

    public void clickActionButton(ActionButton actionButton) {
        element(CONVERSATION_HEADER).findElement(getLocator(actionButton)).click();
        LOGGER.info("{} clicked", actionButton);
    }

    public void moveToFolder(String... subFolders) {
        clickActionButton(MOVE);
        moveConversationMenu.selectDestination(subFolders);
    }

    private By getLocator(ActionButton actionButton) {
        Map<ActionButton, By> locators = new HashMap<>();
        locators.put(ActionButton.FAVORITE, FAVORITE_BUTTON);
        locators.put(ActionButton.MARK_LAST_MESSAGE_UNREAD, MARK_LAST_MESSAGE_UNREAD_BUTTON);
        locators.put(ActionButton.ARCHIVE, ARCHIVE_BUTTON);
        locators.put(ActionButton.TRASH, TRASH_BUTTON);
        locators.put(MOVE, MOVE_BUTTON);
        locators.put(ActionButton.SCHEDULE, SCHEDULE_BUTTON);
        return locators.get(actionButton);
    }
}
