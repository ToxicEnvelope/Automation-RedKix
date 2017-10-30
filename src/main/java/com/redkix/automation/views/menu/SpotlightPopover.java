package com.redkix.automation.views.menu;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpotlightPopover extends RedkixAppView {

    private static final By SPOTLIGHT_POPOVER_CONTAINER = By.className("spotlight-search-popover");

    private static final By COMPOSE_NEW_MESSAGE_BUTTON = By.cssSelector("[ng-click=\"composeMail()\"]");
    private static final By SPOTLIGHT_SEARCH_INPUT = By.id("spotlightSearchInput");

    private static final By SEARCH_RESULTS_NAME = By.cssSelector(".member-info .member-name");
    private static final By SEARCH_RESULTS_EMAIL = By.cssSelector(".member-info .member-email");

    private static final By NO_RESULTS_TITLE = By.cssSelector(".no-results .title");
    private static final By NO_RESULTS_SUB_TITLE = By.cssSelector(".no-results .sub-title");


    public SpotlightPopover(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return SPOTLIGHT_POPOVER_CONTAINER;
    }

    public void composeNewMessage() {
        element(COMPOSE_NEW_MESSAGE_BUTTON).click();
        LOGGER.info("'Compose new message' button clicked");
    }

    public boolean isCursorInSearchField() {
        WebElement searchField = element(SPOTLIGHT_SEARCH_INPUT);
        return getDriver().switchTo().activeElement().equals(searchField);
    }

    public void enterIntoSearchField(String value) {
        element(SPOTLIGHT_SEARCH_INPUT).sendKeys(value);
    }

    public void clickOnEmailInSearchResults(String email) {
        WebElement emailElement = findAll(SEARCH_RESULTS_EMAIL).stream().
                filter(WebElement::isDisplayed).
                filter(e -> email.equals(e.getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("'" + email + "' is not displayed in search results"));

        emailElement.click();
        LOGGER.info("Clicked on '{}' in search results", email);
    }

    public void clickOnNameInSearchResults(String name) {
        WebElement NameElement = findAll(SEARCH_RESULTS_NAME).stream().
                filter(WebElement::isDisplayed).
                filter(e -> name.equals(e.getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("'" + name + "' is not displayed in search results"));

        NameElement.click();
        LOGGER.info("Clicked on '{}' in search results", name);
    }

    public List<String> getDisplayedSearchResultNames() {
        return findAll(SEARCH_RESULTS_NAME).stream().
                filter(WebElement::isDisplayed).
                map(WebElement::getText).
                collect(Collectors.toList());
    }

    public void sendKeys(CharSequence... keys) {
        getDriver().switchTo().activeElement().sendKeys(keys);
    }

    public String[] getNoResultMessages() {
        List<String> messages = new ArrayList<>();
        messages.add(element(NO_RESULTS_TITLE).getText().trim());
        messages.add(element(NO_RESULTS_SUB_TITLE).getText().trim());

        return messages.toArray(new String[0]);
    }




}
