package com.redkix.automation.views.menu;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class MoveConversationMenu extends RedkixAppView {

    private static final By MOVE_CONVERSATION_MENU_CONTAINER = By.className("rxxx-move-popover");
    private static final By EXPAND_COLLAPSE_CHILD = By.className("icon-rx-folder-state");

    public MoveConversationMenu(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return MOVE_CONVERSATION_MENU_CONTAINER;
    }

    public void selectDestination(String... path) {

        WebElement parent = null;

        for (int i = 0; i < path.length; i++) {
            if (parent == null) {
                parent = element(MOVE_CONVERSATION_MENU_CONTAINER);
            }

            String folder = path[i];

            WebElement folderOption = parent.findElements(By.tagName("li")).stream().
                    filter(e -> e.getText().equals(folder)).findFirst().
                    orElseThrow(() -> new NoSuchElementException("Folder '" + folder + "' is not available"));

            //if last in the path, click on it, otherwise try to click on expand icon & repeat search
            if (i == path.length - 1) {
                folderOption.click();
                LOGGER.info("Selected '{}' folder as destination", folder);
            }
            else {
                folderOption.findElements(EXPAND_COLLAPSE_CHILD).stream().
                        filter(WebElement::isDisplayed).
                        findFirst().orElseThrow(() -> new NoSuchElementException("'>' is not available")).
                        click();
                LOGGER.info("'>' clicked to expand {} folder's children", folder);
                parent = folderOption;
            }
        }
    }



}
