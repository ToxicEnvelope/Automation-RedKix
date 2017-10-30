package com.redkix.automation.media;

import com.redkix.automation.RedKixTests;
import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.helper.FileHelper;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.User;
import com.redkix.automation.steps.*;
import com.redkix.automation.utils.Commons;
import com.redkix.automation.utils.category.Sanity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.redkix.automation.model.EmailServiceType.EXCHANGE;

@RunWith(SerenityRunner.class)
public class AttachmentsTests extends RedKixTests {

    @Steps
    public LoginSteps loginSteps;

    @Steps
    public CreateEmailSteps createEmailSteps;

    @Steps
    public NavigationSteps navigationSteps;

    @Steps
    public EmailServiceSteps emailServiceSteps;


    @Test
    @Category(Sanity.class)
    public void C219341_Compose_Reply_Attachments_Add_files() {

        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        loginSteps.loginToApp(user1);

        navigationSteps.clickEmailLink();
        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail(user2.getEmail());
        createEmailSteps.fillEmailDetails(email);

        List<File> attachments = IntStream.range(0, 10).boxed().
                map(i -> FileHelper.getTmpFile()).
                collect(Collectors.toList());

        for (File attachment : attachments) {
            createEmailSteps.addAttachment(attachment);
        }

        File removedAttachment = Commons.getAny(attachments);
        createEmailSteps.removeAttachment(removedAttachment.getName());
        createEmailSteps.clickSendButton();

        List<String> expectedAttachments = attachments.stream().
                filter(a -> !a.equals(removedAttachment)).
                map(File::getName).
                collect(Collectors.toList());

        emailServiceSteps.verifyThatListOfAttachmentsForEmailMatchesTo(user2, email, expectedAttachments);
    }


    @Test
    @Category(Sanity.class)
    public void C24714_Compose__Reply_Attachments_Remove_files() {
        User user1 = UserHelper.getUser(EXCHANGE);
        User user2 = UserHelper.getUser(EXCHANGE, user1);

        loginSteps.loginToApp(user1);
        navigationSteps.clickEmailLink();
        navigationSteps.openComposeNewMessageView();

        Email email = EmailHelper.createEmail(user2.getEmail());
        createEmailSteps.fillEmailDetails(email);

        File attachment = FileHelper.getTmpFile();
        createEmailSteps.addAttachment(attachment);
        createEmailSteps.removeAttachment(attachment.getName());

        createEmailSteps.verifyThatListOfAttachmentsMatchesTo(Collections.emptyList());

        createEmailSteps.clickSendButton();

        emailServiceSteps.verifyThatListOfAttachmentsForEmailMatchesTo(user2, email, Collections.emptyList());
    }





}
