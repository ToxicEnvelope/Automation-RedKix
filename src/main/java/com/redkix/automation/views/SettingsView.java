package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SettingsView extends RedkixAppView {

    private final By SETTINGS_MODAL = By.className("settings-modal");
    private final By DONE_BTN = By.cssSelector("[ng-click=\"closeThisDialog('SAVE')\"]");
    private final By LOG_OUT_BTN = By.className("rx-btn-red");
    private final By CLOSE_BTN = By.cssSelector(".settings-modal [ng-click=\"closeThisDialog()\"]");

    private final By PRIORITY_INBOX_CHECKBOX = By.cssSelector("input[ng-model=\"userPreferences.priorityInbox\"]");
    private final By PRIORITY_INBOX_ICON = By.cssSelector("span[ng-model='userPreferences.priorityInbox']");

    public SettingsView(WebDriver driver) {
        super(driver);
    }

    public void logOut() {
        element(LOG_OUT_BTN).click();
        LOGGER.info("'Log out' button clicked");
    }

    @Override
    protected By getViewIdentifier() {
        return SETTINGS_MODAL;
    }

    public void setPriorityInbox(boolean expectedState) {
        boolean priorityInboxCurrentState = getSettingsState(PRIORITY_INBOX_CHECKBOX);

        if (expectedState == priorityInboxCurrentState) {
            LOGGER.info("'Priority Inbox' setting is already {}", (expectedState ? "enabled":"disabled"));
            return;
        }

        element(PRIORITY_INBOX_ICON).click();
        LOGGER.info("Changed 'Priority Inbox' setting to be {}", (expectedState ? "enabled":"disabled"));
    }

    public void close() {
        element(CLOSE_BTN).click();
        LOGGER.info("Clicked on 'x' in Settings modal");
    }

    public void clickDone() {
        element(DONE_BTN).click();
        LOGGER.info("Clicked on 'Done' in Settings modal");
    }

    private boolean getSettingsState(By locator) {
        return (boolean) getJavascriptExecutorFacade().executeScript("return arguments[0].checked",
                element(locator));
    }
}
