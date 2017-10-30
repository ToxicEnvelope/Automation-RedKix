package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class ContactCardView extends RedkixAppView {

    private final By EMAIL_CONTAINER = By.cssSelector(".contact-card-header .contact-card-email");

    public ContactCardView(WebDriver driver) {
        super(driver);
    }

    public String getEmail() {
        return element(EMAIL_CONTAINER).getText();
    }

    public void waitUntilHidden() {
        waitFor(ExpectedConditions.invisibilityOfElementLocated(EMAIL_CONTAINER));
    }

    @Override
    protected By getViewIdentifier() {
        return EMAIL_CONTAINER;
    }
}
