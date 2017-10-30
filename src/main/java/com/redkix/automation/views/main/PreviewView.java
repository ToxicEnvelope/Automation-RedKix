package com.redkix.automation.views.main;

import com.google.common.base.Predicate;
import com.redkix.automation.RedkixAppView;
import com.redkix.automation.model.Email;
import com.redkix.automation.views.menu.ContextMenu;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.redkix.automation.views.menu.ContextMenu.ContextMenuOption.*;

public class PreviewView extends RedkixAppView {

    private final By PREVIEW_PANEL = By.id("previewsPane");
    private final By PREVIEW_ITEM = By.className("rxxx-preview-repeater");
    private final By PREVIEW_SUBJECT = By.className("preview-subject");
    private final By PREVIEW_FIRST_LINE = By.className("rxxx-first-line");
    private final By PREVIEW_UNREAD_MARKER = By.className("rx-unread");
    private final By PREVIEW_SELECTED_ITEM = By.cssSelector(".rx-preview-item.selected");
    private final By PREVIEW_PANEL_SPINNER = By.cssSelector("#previewsPane .icon-rx-loading-spinner");
    private final By PREVIEW_TABS = By.cssSelector("#previewsTabs .tabs");

    private final By SEARCH_ALL_CONVERSATIONS_INPUT = By.cssSelector(".search-box input");
    private final By CLEAR_SEARCH_BUTTON = By.cssSelector("[ng-click=\"ctrl.clearSearchString()\"]");

    private final By NO_CONVERSATION_MESSAGE_CONTAINER = By.cssSelector("#previewsPane [ng-if='isEmpty'] .message");
    private final By NO_CONVERSATION_DETAIL_CONTAINER = By.cssSelector("#previewsPane [ng-if='isEmpty'] .details");

    public enum PreviewTab {
        INBOX,
        PRIORITY,
        OTHER
    }


    private ContextMenu contextMenu;

    public PreviewView(WebDriver driver) {
        super(driver);
    }

    public void selectEmailBySubject(String subject) {
        WebElementFacade emailElement = findPreviewItem(subject);
        emailElement.click();
        LOGGER.info("Clicked on email with subject '{}'", subject);
    }

    public List<String> getDisplayedSubjects() {
        return findAll(PREVIEW_ITEM).stream().
                filter(WebElement::isDisplayed).
                filter(item -> item.findElements(PREVIEW_SUBJECT).stream().anyMatch(WebElement::isDisplayed)).
                map(item -> item.findElement(PREVIEW_SUBJECT).getText()).
                collect(Collectors.toList());
    }


    @Override
    protected By getViewIdentifier() {
        return PREVIEW_PANEL;
    }

    @Override
    protected Predicate<WebDriver> ready() {
        //return d -> d.findElements(PREVIEW_SUBJECT).stream().anyMatch(WebElement::isDisplayed);
        return d -> d.findElements(PREVIEW_PANEL_SPINNER).stream().noneMatch(WebElement::isDisplayed);
    }

    public boolean isRead(Email email) {
        WebElementFacade emailElement = findPreviewItem(email.getSubject());
        return emailElement.findElements(PREVIEW_UNREAD_MARKER).stream().
                noneMatch(WebElement::isDisplayed);
    }

    public void setIsRead(Email email, boolean isRead) {
        WebElementFacade emailElement = findPreviewItem(email.getSubject());
        withAction().contextClick(emailElement.findElement(PREVIEW_FIRST_LINE)).build().perform();

        contextMenu.selectOption(isRead? MARK_AS_READ : MARK_AS_UNREAD);
    }

    private WebElementFacade findPreviewItem(String subject) {
        return findAll(PREVIEW_ITEM).stream().
                filter(WebElementFacade::isDisplayed).
                filter(e -> subject.equals(e.findElement(PREVIEW_SUBJECT).getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Email with subject '" + subject + "' is not available"));
    }


    public Optional<String> getSelectedEmailSubject() {
        return findAll(PREVIEW_ITEM).stream().
                filter(WebElement::isDisplayed).
                filter(e -> e.findElements(PREVIEW_SELECTED_ITEM).size() > 0).
                map(e -> e.findElement(PREVIEW_SUBJECT).getText()).findFirst();
    }

    public void moveConversationTo(Email email, String destination) {
        WebElementFacade emailElement = findPreviewItem(email.getSubject());
        withAction().contextClick(emailElement.findElement(PREVIEW_FIRST_LINE)).build().perform();
        contextMenu.selectChildOption(MOVE_TO, destination);
    }

    public void searchAll(String searchTerm) {
        element(SEARCH_ALL_CONVERSATIONS_INPUT).sendKeys(searchTerm, Keys.ENTER);
        LOGGER.info("{} entered into 'Search all conversations'", searchTerm);
    }

    public void clearSearchField() {
        element(CLEAR_SEARCH_BUTTON).click();
        LOGGER.info("Clear search (x) button clicked");
    }

    public String[] getEmptyResultsDetailedMessage() {
        List<String> message = new ArrayList<>();
        message.add(element(NO_CONVERSATION_MESSAGE_CONTAINER).getText().trim());
        message.add(element(NO_CONVERSATION_DETAIL_CONTAINER).getText().trim());
        return message.toArray(new String[message.size()]);
    }

    public List<PreviewTab> getDisplayedTabs() {
        return findAll(PREVIEW_TABS).stream().
                filter(WebElement::isDisplayed).
                map(WebElement::getText).
                map(String::trim).
                map(s -> PreviewTab.valueOf(s.toUpperCase())).
                collect(Collectors.toList());
    }
}
