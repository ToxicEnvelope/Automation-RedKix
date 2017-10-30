package com.redkix.automation.steps;

import com.redkix.automation.views.ChatView;
import com.redkix.automation.views.main.NavigationView;
import com.redkix.automation.views.menu.SpotlightPopover;
import net.thucydides.core.annotations.Step;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpotlightSteps extends RedkixScenarioSteps {

    private NavigationView navigationView;
    private SpotlightPopover spotlightPopover;
    private ChatView chatView;

    @Step("Click cmd(ctrl) + 9 to open spotlight")
    public void openSpotlightByHotKeys() {
        navigationView.openSpotlightByKeys();
    }

    @Step
    public void checkThatCursorInSearchField() {
        assertTrue("Cursor not in search field", spotlightPopover.isCursorInSearchField());
    }

    @Step
    public void enterIntoSearchField(String value) {
        spotlightPopover.enterIntoSearchField(value);
    }

    @Step
    public void clickOnEmailInSearchResults(String email) {
        spotlightPopover.clickOnEmailInSearchResults(email);
    }

    @Step
    public void clickOnNameInSearchResults(String name) {
        spotlightPopover.clickOnNameInSearchResults(name);
    }

    @Step
    public void checkThatChatNameCorrespondsToExpected(String expectedName) {
        assertEquals(expectedName, chatView.getConversationSubject());
    }

    @Step
    public String getDisplayedSearchResultNames(int index) {
        List<String> results = spotlightPopover.getDisplayedSearchResultNames();

        if (results.size() < index - 1){
            throw new IllegalArgumentException("There is only " + results.size() + " results displayed");
        }

        return results.get(index - 1);
    }

    @Step
    public void sendKeyToSpotlight(CharSequence... keys) {
        spotlightPopover.sendKeys(keys);
    }

    @Step
    public void checkThatNoResultsTitleMatchesTo(String... expected) {
        assertArrayEquals(expected, spotlightPopover.getNoResultMessages());
    }
}
