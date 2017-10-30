package com.redkix.automation.views.messages;

import com.google.common.base.Function;
import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DesktopNotificationsMessage extends RedkixAppView {

    public enum Button {
        ENABLE,
        NOT_NOW
    }

    private static final By MESSAGE_CONTAINER = By.className("cg-notify-message");

    public DesktopNotificationsMessage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return MESSAGE_CONTAINER;
    }

    public void clickButton(Button button) {

        try {
            Function<WebDriver, WebElement> buttonAvailable = d ->
                    element(MESSAGE_CONTAINER).findElements(By.tagName("button")).stream().
                            filter(WebElement::isDisplayed).
                            filter(e -> button.name().replace("_", " ").equalsIgnoreCase(e.getText())).
                            filter(e -> e.getLocation().getY() > 0).
                            findFirst().orElse(null);

            WebElement buttonElement = waiter().until(buttonAvailable);
            buttonElement.click();
            LOGGER.info("'{}' clicked in 'Enable desktop notifications' message", button);
        }
        catch (TimeoutException e) {
            throw new NoSuchElementException("ActionButton " + button + " is not available");
        }
    }
}
