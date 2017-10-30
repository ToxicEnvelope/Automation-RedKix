package com.redkix.automation.views;


import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MembersView extends RedkixAppView {

    private static final By MEMBERS_VIEW_CONTAINER = By.className("members-widget");
    private static final By ADD_MEMBERS_INPUT = By.cssSelector(".add-members-input-container input");
    private static final By SEND_INVITE_BUTTON = By.cssSelector("[ng-click=\"ctrl.addMembers()\"]");

    public MembersView(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return MEMBERS_VIEW_CONTAINER;
    }

    public void addMembers(String... emails) {
        WebElement addMembersInput = element(ADD_MEMBERS_INPUT);

        for (String email : emails) {
            addMembersInput.sendKeys(email, Keys.ENTER);
            LOGGER.info("{} entered in 'Add members'", email);
        }

        element(SEND_INVITE_BUTTON).click();
        LOGGER.info("'Send invite' button clicked");
    }
}
