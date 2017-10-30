package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class MessageDetailView extends RedkixAppView {

    public enum CardFor {
        TO,
        CC,
        BCC
    }

    private final By MESSAGE_DETAIL = By.className("message-details");
    private final By RECIPIENT_TYPE = By.className("recipient-type");
    private final By TO_SECTION = By.cssSelector("[ng-if=\"message.toMailBoxes.length > 0\"]");
    private final By CC_SECTION = By.cssSelector("[ng-if=\"message.ccMailBoxes.length > 0\"]");
    private final By BCC_SECTION = By.cssSelector("[ng-if=\"message.bccMailBoxes.length > 0\"]");
    private final By CONTACT_AREA = By.className("contact-chip");
    private final By SUBJECT = By.id("conversationSubjectHeader");


    private ContactCardView contactCardView;


    public MessageDetailView(WebDriver driver) {
        super(driver);
    }

    public void openContactCard(CardFor cardFor) {
        By locator = getLocatorForSection(cardFor);

        WebElementFacade section = element(locator);

        withAction().release().
                moveToElement(section.findElement(RECIPIENT_TYPE)).
                moveToElement(section.findElement(CONTACT_AREA)).
                build().
                perform();

        LOGGER.info("Move to '{}' area", cardFor.name());
    }

    public void closeContactCard() {
        withAction().moveToElement(element(SUBJECT)).click().build().perform();
        contactCardView.waitUntilHidden();
    }

    public boolean isSectionPresent(CardFor cardFor) {
        By locator = getLocatorForSection(cardFor);

        return findAll(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private By getLocatorForSection(CardFor cardFor) {
        if (cardFor == CardFor.TO) {
            return TO_SECTION;
        }
        if (cardFor == CardFor.CC) {
            return CC_SECTION;
        }

        return BCC_SECTION;
    }

    @Override
    protected By getViewIdentifier() {
        return MESSAGE_DETAIL;
    }
}
