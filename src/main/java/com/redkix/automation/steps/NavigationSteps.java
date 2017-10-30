package com.redkix.automation.steps;

import com.redkix.automation.model.Folder;
import com.redkix.automation.views.SettingsView;
import com.redkix.automation.views.main.NavigationView;
import com.redkix.automation.views.menu.SpotlightPopover;
import net.thucydides.core.annotations.Step;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class NavigationSteps extends RedkixScenarioSteps {

    private NavigationView navigationView;
    private SettingsView settingsView;//TODO: time to move this to Settings steps
    private SpotlightPopover spotlightPopover;

    @Step
    public void openComposeNewMessageView() {
        clickSpotlightButton();
        clickCreateNewMessageButton();
    }

    @Step
    public void clickSpotlightButton() {
        navigationView.openSpotlight();
    }

    @Step
    public void clickCreateNewMessageButton() {
        spotlightPopover.composeNewMessage();
    }

    @Step
    public void openFolders() {
        navigationView.openFolders();
    }

    @Step
    public void openFolder(Folder folder) {
        navigationView.openFolder(folder);
    }

    @Step
    public void openFolder(String path) {
        navigationView.openFolder(path.split("->"));
    }

    @Step
    public void logOut() {
        navigationView.openSettingsView();
        settingsView.logOut();
    }

    @Step
    public void hoverMouseOverCreateMessageButton() {
        navigationView.hoverMouseOnSpotlightButton();
    }

    @Step
    public void checkThatTooltipWithTextDisplayed(String expectedText) {
        assertEquals(expectedText, navigationView.getTooltipText());
    }

    public List<String> getPinnedConversations() {
        return navigationView.getConversations();
    }

    @Step
    public void selectPinnedConversation(String conversation) {
        navigationView.selectConversation(conversation);
    }

    @Step
    public void verifyThatHighlightedPinnedConversationMatchesTo(String conversation) {
        assertEquals(conversation, navigationView.getSelectedConversation());
    }

    @Step
    public void clickEmailLink() {
        navigationView.clickEmailLink();
    }

    @Step
    public void openBrowseChannels() {
        navigationView.clickBrowseChannelsButton();
    }


    @Step
    public void verifyThatChannelsAreDisplayed(String... channels) {
        waitForResult(chs -> chs.size() > 0, navigationView.getChannels());

        List<String> displayedChannels = navigationView.getChannels();

        for (String channel : channels) {
            assertTrue("Channel " + channel + " is not displayed in the list of Channels",
                    displayedChannels.contains(channel));
        }
    }

    @Step
    public void verifyThatPersonIsDisplayed(String person) {
        assertTrue("Person " + person + " is not displayed in the list of People",
                navigationView.getPeople().contains(person));
    }

    @Step
    public List<String> getChannels() {
        return navigationView.getChannels();
    }

    @Step
    public void checkThatValueInSearchFieldMatchesTo(String expected) {
        assertEquals(expected, navigationView.getSearchFieldValue());
    }

    @Step
    public void checkThatHighlightedFolderIs(String expectedFolder) {
        assertEquals(expectedFolder, navigationView.getSelectedFolder().get());
    }
}
