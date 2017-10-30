package com.redkix.automation.channel;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.EmailServiceType;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import com.redkix.automation.utils.category.Sanity;
import com.redkix.automation.views.ChannelView;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static com.redkix.automation.model.EmailServiceType.GMAIL;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@RunWith(SerenityRunner.class)
public class ChannelTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public ChannelSteps channelSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public PreviewSteps previewSteps;

    @Steps
    public EmailServiceSteps emailServiceSteps;

    public static final String NO_RESULTS_FOUND_TEXT = "NO RESULTS FOUND";
    public static final String NO_CHANNELS_MATCHING_YOUR_SEARCH_FOUND_TEXT = "No channels matching your search were found.";

    @Test
    @Category(Sanity.class)
    public void CXXXX_Search_and_create_channels_test() {
        User user1 = UserHelper.getUserByEmail("qatest4@redkix.com");
        User user2 = UserHelper.getUser(EXCHANGE, user1);
        User externalUser = UserHelper.getExternalUser();

        loginSteps.loginToApp(user1);
        //previewSteps.clickNotNowInTheEnableDesktopNotificationsMessage();

        navigationSteps.openBrowseChannels();
        channelSteps.searchFor("random");
        channelSteps.verifyThatChannelWithTitleIsAvailableInSearchResults("Random");

        channelSteps.searchFor(randomAlphabetic(10));
        channelSteps.verifyThatMessagesAreDisplayedInSearchResults(
                Arrays.asList(NO_RESULTS_FOUND_TEXT, NO_CHANNELS_MATCHING_YOUR_SEARCH_FOUND_TEXT));

        String channelName = randomAlphabetic(5);

        channelSteps.createNewChannel(channelName, true);

        channelSteps.clickInviteOthersLink();
        channelSteps.addMembersToChannel(user2.getEmail(), externalUser.getEmail());
        channelSteps.clickOnChannelAvatar();

        channelSteps.sendMessageInChannel(randomAlphabetic(5));
        String subject = String.format("Re: %s Channel", channelName);
        emailServiceSteps.checkThatEmailExists(externalUser, new Email().setSubject(subject));

        channelSteps.switchToTab(ChannelView.Tab.THREADS);
        String threadName = randomAlphabetic(5);
        channelSteps.createNewThread(threadName);
        channelSteps.sendMessageInThread(randomAlphabetic(5));

        emailServiceSteps.checkThatEmailExists(externalUser, new Email().setSubject("Re: " + threadName));
    }

    @Test
    @Category(Sanity.class)
    public void CXXXX_Favorite_channels_test() {
        User user = new User("Automator1@redkixgmail.com", "Redkix999").setServiceType(GMAIL);

        loginSteps.loginToApp(user);

        navigationSteps.verifyThatChannelsAreDisplayed("General", "Random");

        navigationSteps.verifyThatPersonIsDisplayed("Redford");
    }




}
