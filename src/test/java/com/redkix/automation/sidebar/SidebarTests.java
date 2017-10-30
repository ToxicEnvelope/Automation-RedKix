package com.redkix.automation.sidebar;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.CreateEmailSteps;
import com.redkix.automation.steps.LoginSteps;
import com.redkix.automation.steps.NavigationSteps;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX;

@RunWith(SerenityRunner.class)
public class SidebarTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    public static final String COMPOSE_AND_SPOTLIGHT_TEXT = "Compose & Spotlight (%s + n)";

    @Test
    @Category(Sanity.class)
    public void C219150_Sidebar_Create_a_Message_Press_Button() {
        User user = UserHelper.getUser(EXCHANGE);
        loginSteps.openLoginView();
        loginSteps.submitEmailAndClickGetStarted(user.getEmail());
        loginSteps.signIn(user.getEmail(), user.getPassword());

        navigationSteps.hoverMouseOverCreateMessageButton();

        String expectedText = String.format(COMPOSE_AND_SPOTLIGHT_TEXT, IS_OS_MAC_OSX ? "⌘" : "ctrl");

        navigationSteps.checkThatTooltipWithTextDisplayed(expectedText);

        navigationSteps.clickEmailLink();

        navigationSteps.openComposeNewMessageView();

        createEmailSteps.checkThatTypingCursorSetForToField();

        createEmailSteps.pressEsc();

        createEmailSteps.checkThatComposeEmailViewClosed();
    }
}
