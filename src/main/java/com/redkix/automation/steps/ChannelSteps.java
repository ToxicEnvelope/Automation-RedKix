package com.redkix.automation.steps;

import com.redkix.automation.views.BrowseChannelsListView;
import com.redkix.automation.views.ChannelView;
import net.thucydides.core.annotations.Step;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChannelSteps extends RedkixScenarioSteps {

    private BrowseChannelsListView browseChannelsListView;
    private ChannelView channelView;

    @Step
    public void searchFor(String searchTerm) {
        browseChannelsListView.search(searchTerm);
    }

    @Step
    public void verifyThatChannelWithTitleIsAvailableInSearchResults(String channelTitle) {
        assertTrue("Channel '" + channelTitle + "' is not displayed",
                browseChannelsListView.getDisplayedChannelTitles().contains(channelTitle));
    }

    @Step
    public void verifyThatMessagesAreDisplayedInSearchResults(List<String> expectedTexts) {
        assertEquals(expectedTexts, browseChannelsListView.getEmptySearchResultsMessage());
    }

    @Step
    public void createNewChannel(String channelName, boolean isPrivate) {
        browseChannelsListView.createNewChannel(channelName, isPrivate);
    }

    @Step
    public void createNewChannel(String channelName, boolean isPrivate, String... invitees) {
        browseChannelsListView.createNewChannel(channelName, isPrivate, invitees);
    }

    @Step
    public void switchToTab(ChannelView.Tab tab) {
        channelView.switchToTab(tab);
    }

    @Step
    public void createNewThread(String subject) {
        channelView.createNewThread(subject);
    }

    @Step
    public void sendMessageInThread(String message) {
        channelView.replyAll(message);
    }

    @Step
    public void clickInviteOthersLink() {
        channelView.clickInviteOthersLink();
    }

    @Step
    public void addMembersToChannel(String... emails) {
        channelView.addMembersToChannel(emails);
    }

    @Step
    public void clickOnChannelTitle() {
        channelView.clickOnChannelTitle();
    }

    @Step
    public void clickOnChannelAvatar() {
        channelView.clickOnChannelAvatar();
    }

    @Step
    public void sendMessageInChannel(String message) {
        channelView.replyAll(message);
    }

    @Step
    public void checkThatChannelTitleMatchesTo(String expectedTitle) {
        assertEquals(expectedTitle, channelView.getChannelTitle());
    }

}
