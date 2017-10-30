package com.redkix.automation.spotlight;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import com.redkix.automation.utils.Commons;
import com.redkix.automation.views.main.PreviewView;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;

import java.util.List;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@RunWith(SerenityRunner.class)
public class SpotlightTests extends RedKixTests {

    public static final String NO_PEOPLE_CHANNELS_FOUND_TEXT = "NO PEOPLE & CHANNELS FOUND";
    public static final String PRESS_SHIFT_ENTER_TO_SEARCH_ALL_CONVERSATIONS_TEXT = "Press Shift+Enter to search all conversations.";
    public static final String NO_CONVERSATIONS_TEXT = "NO CONVERSATIONS";
    public static final String THIS_FOLDER_DOESNT_CONTAIN_ANY_CONVERSATION_TEXT = "This folder doesn't contain any conversations.";

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public SpotlightSteps spotlightSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public ChannelSteps channelSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public SettingsSteps settingsSteps;


    @Test
    public void test_C225598_Spotlight_curser_focus() {

        User user = UserHelper.getUser();

        loginSteps.loginToApp(user);

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.checkThatCursorInSearchField();
    }

    @Test
    public void test_C225599_Spotlight_search_people_Enter() {

        User user1 = UserHelper.getUserByEmail("qatest4@redkix.com");
        User user2 = UserHelper.getUserByEmail("qatest6@redkix.com");

        loginSteps.loginToApp(user1);

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.enterIntoSearchField(user2.getEmail());
        spotlightSteps.clickOnEmailInSearchResults(user2.getEmail());

        spotlightSteps.checkThatChatNameCorrespondsToExpected("Qatest 6");
    }

    @Test
    public void test_225600_Spotlight_search_people_arrows() {
        User user = UserHelper.getUserByEmail("qatest4@redkix.com");

        loginSteps.loginToApp(user);

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.enterIntoSearchField("qatest");

        int searchResultIndex = 3;

        String displayedName = spotlightSteps.getDisplayedSearchResultNames(searchResultIndex);

        spotlightSteps.sendKeyToSpotlight(Keys.ARROW_DOWN, Keys.ARROW_DOWN);

        spotlightSteps.sendKeyToSpotlight(Keys.ENTER);

        spotlightSteps.checkThatChatNameCorrespondsToExpected(displayedName);
    }


    @Test
    public void test_C225601_Spotlight_search_channels_Enter() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        List<String> channels = navigationSteps.getChannels();

        String channel = Commons.getAny(channels);

        spotlightSteps.openSpotlightByHotKeys();

        int symbols = Commons.getRandom(1, channel.length() - 1);

        spotlightSteps.enterIntoSearchField(channel.substring(0, symbols));

        spotlightSteps.clickOnNameInSearchResults(channel);

        channelSteps.checkThatChannelTitleMatchesTo(channel);
    }


    @Test
    public void test_C225602_Spotlight_search_channels_arrows() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        List<String> channels = navigationSteps.getChannels();

        String channel = Commons.getAny(channels);

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.enterIntoSearchField(channel);

        spotlightSteps.sendKeyToSpotlight(Keys.ARROW_DOWN, Keys.ENTER);

        channelSteps.checkThatChannelTitleMatchesTo(channel);
    }

    @Test
    public void test_C225603_Spotlight_search_folders_inbox() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        settingsSteps.openSettings();
        settingsSteps.setPriorityInbox(false);
        settingsSteps.saveSettings();

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.enterIntoSearchField("inbox");
        spotlightSteps.sendKeyToSpotlight(Keys.ENTER);

        previewSteps.checkThatDisplayedTabsMatchTo(PreviewView.PreviewTab.INBOX);
    }

    @Test
    public void test_C225604_Spotlight_search_folders_archive() {
        User user = UserHelper.getUserByEmail("qatest7@redkix.com");

        loginSteps.loginToApp(user);

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.enterIntoSearchField("archive");

        spotlightSteps.sendKeyToSpotlight(Keys.ENTER);

        navigationSteps.checkThatHighlightedFolderIs("Archive");
    }

    @Test
    public void test_C225605_Spotlight_open_composer() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        navigationSteps.clickEmailLink();

        spotlightSteps.openSpotlightByHotKeys();

        spotlightSteps.sendKeyToSpotlight(Keys.ENTER);

        createEmailSteps.checkThatTypingCursorSetForToField();
    }

    @Test
    public void test_C225606_Spotlight_no_results_placeholder() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        spotlightSteps.openSpotlightByHotKeys();

        String searchTerm = randomAlphabetic(5);

        spotlightSteps.enterIntoSearchField(searchTerm);

        spotlightSteps.checkThatNoResultsTitleMatchesTo(NO_PEOPLE_CHANNELS_FOUND_TEXT,
                PRESS_SHIFT_ENTER_TO_SEARCH_ALL_CONVERSATIONS_TEXT);

        spotlightSteps.sendKeyToSpotlight(Keys.chord(Keys.SHIFT, Keys.ENTER));

        navigationSteps.checkThatValueInSearchFieldMatchesTo(searchTerm);
    }

    @Test
    public void test_C225607_search_all_conversations_no_results() {
        User user = UserHelper.getUser(EXCHANGE);

        loginSteps.loginToApp(user);

        spotlightSteps.openSpotlightByHotKeys();

        String searchTerm = randomAlphabetic(5);

        spotlightSteps.enterIntoSearchField(searchTerm);

        spotlightSteps.sendKeyToSpotlight(Keys.chord(Keys.SHIFT, Keys.ENTER));

        previewSteps.checkThatNoResultsMessageInPreviewMatchesTo(NO_CONVERSATIONS_TEXT,
                THIS_FOLDER_DOESNT_CONTAIN_ANY_CONVERSATION_TEXT);
    }

    @Test
    public void test_C225608_Spotlight_search_all_conversations() {

        User user1 = UserHelper.getUserByEmail("qatest4@redkix.com");
        User user2 = UserHelper.getUserByEmail("qatest6@redkix.com");

        loginSteps.loginToApp(user1);
        navigationSteps.clickEmailLink();
        previewSteps.search(user2.getEmail());
        List<String> subjects = previewSteps.getDisplayedSubjects();
        previewSteps.clearSearchField();

        spotlightSteps.openSpotlightByHotKeys();
        spotlightSteps.enterIntoSearchField(user2.getEmail());
        spotlightSteps.sendKeyToSpotlight(Keys.chord(Keys.SHIFT, Keys.ENTER));

        previewSteps.verifyThatDisplayedSubjectsMatches(subjects);
    }


}
