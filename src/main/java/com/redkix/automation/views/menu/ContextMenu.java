package com.redkix.automation.views.menu;


import com.google.common.base.Objects;
import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ContextMenu extends RedkixAppView {

    public enum ContextMenuOption {
        MARK_AS_READ("Mark as Read"),
        MARK_AS_UNREAD("Mark as Unread"),
        FAVORITE_CONVERSATION("Favorite Conversation"),
        ARCHIVE("Archive"),
        MOVE_TO_TRASH("Move To Trash"),
        MOVE_TO("Move To");

        private String text;

        ContextMenuOption(String text) {
            this.text = text;
        }
    }

    private static final By CONTEXT_MENU_CONTAINER = By.cssSelector(".rx-context-menu ul");

    public ContextMenu(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return CONTEXT_MENU_CONTAINER;
    }

    public void selectOption(String optionText) {
        WebElement optionElement = findOption(optionText);
        optionElement.click();
        LOGGER.info("'{}' clicked in context menu", optionText);
    }


    public void selectChildOption(ContextMenuOption parentOption, String childOption) {
        WebElement parentOptionElement = findOption(parentOption.text);
        withAction().moveToElement(parentOptionElement).perform();
        LOGGER.info("Moved mouse over '{}' option", parentOption);

        selectOption(childOption);
    }

    public void selectOption(ContextMenuOption option) {
        selectOption(option.text);
    }


    private WebElement findOption(String optionText) {
        return findAll(CONTEXT_MENU_CONTAINER).stream().
                map(e -> e.findElements(By.tagName("li"))).
                flatMap(l -> l.stream()).filter(option -> Objects.equal(optionText, option.getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Can't find option with text " + optionText));
    }
}
