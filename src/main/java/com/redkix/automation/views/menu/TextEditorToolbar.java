package com.redkix.automation.views.menu;

import com.redkix.automation.RedkixAppView;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class TextEditorToolbar extends RedkixAppView {

    public enum EditorAction {
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        LINK("createLink"),
        H2("header type two"),
        H3("header type three");

        private String dataAction;

        EditorAction(String dataAction) {
            this.dataAction = dataAction;
        }

        public String getDataAction() {
            return dataAction;
        }
    }

    private static final By TOOLBAR_CONTAINER = By.className("medium-editor-toolbar-actions");
    private static final By LINK_INPUT = By.className("medium-editor-toolbar-input");
    private static final By SAVE_LINK_BUTTON = By.className("medium-editor-toolbar-save");
    private static final By CLOSE_LINK_BUTTON = By.className("medium-editor-toolbar-close");

    public TextEditorToolbar(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By getViewIdentifier() {
        return TOOLBAR_CONTAINER;
    }

    public void apply(EditorAction action, String ... extra) {
        WebElement container = findAll(TOOLBAR_CONTAINER).stream().
                filter(WebElement::isDisplayed).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Editor toolbar is not displayed"));
        container.findElement(By.cssSelector("[data-action='" + action.getDataAction() + "']")).
                click();
        LOGGER.info("'{}' clicked in editor toolbar", action);

        if (action == EditorAction.LINK && extra != null) {
            getDriver().findElements(LINK_INPUT).stream().
                    filter(WebElement::isDisplayed).
                    findFirst().orElseThrow(()-> new NoSuchElementException("No field to enter href")).
                    sendKeys(extra[0]);
            LOGGER.info("'{}' entered into link field", extra[0]);

            getDriver().findElements(SAVE_LINK_BUTTON).stream().filter(WebElement::isDisplayed).
                    findFirst().orElseThrow(()-> new NoSuchElementException("No Save button")).
                    click();
            LOGGER.info("'Save' button clicked in Link modal");
        }
    }



}
