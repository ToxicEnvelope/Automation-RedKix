package com.redkix.automation.steps;


import com.redkix.automation.model.Email;
import com.redkix.automation.model.EmailServiceType;
import com.redkix.automation.model.Folder;
import com.redkix.automation.model.User;
import com.redkix.automation.services.EmailService;
import com.redkix.automation.services.ExchangeEmailService;
import com.redkix.automation.services.GmailEmailService;
import net.thucydides.core.annotations.Step;

import java.util.Collections;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.junit.Assert.assertEquals;

public class EmailServiceSteps extends RedkixScenarioSteps {

    private EmailService emailService;

    @Step
    public void markEmailAsReadViaService(User user, Email email) {
        service(user).markAsRead(email);
    }

    @Step
    public void markEmailAsUnreadViaService(User user, Email email) {
        service(user).markAsUnread(email);
    }

    @Step
    public void checkEmailStateCorrespondsToExpected(User user, Email email, boolean isRead) {
        boolean result = waitForResult(e -> service(user).isRead(e) == isRead, email);

        assertTrue("Email 'is read' state doesn't correspond to expected", result);
    }

    @Step
    public void sendEmail(User sender, Email email) {
        service(sender).send(email);
    }

    @Step
    public void replyToEmail(User user, Email originalEmail, String body) {
        waitForResult(e -> service(user).search(originalEmail) > 0, originalEmail);
        service(user).reply(originalEmail, body, true);
    }

    @Step
    public void checkThatEmailExists(User user, Email email) {
        assertTrue("Email " + email.getSubject() + " doesn't exist",
                waitForResult(e -> service(user).search(email) > 0, email));
    }

    @Step
    public void checkThatEmailExists(User user, Email email, Folder folder) {
        assertTrue("Email '" + email.getSubject() + "' doesn't exist in folder " + folder,
                waitForResult(e -> service(user).searchInFolder(email, folder) > 0, email));
    }

    @Step
    public void checkThatEmailsExists(User user, Email email, int quantity) {
        assertTrue(quantity + " email(s) '" + email.getSubject() + "' are not found",
                waitForResult(e -> service(user).search(email) == quantity, email));
    }

    @Step
    public void verifyThatListOfAttachmentsForEmailMatchesTo(User user, Email email, List<String> fileNames) {
        EmailService service = service(user);
        waitForResult(e -> service.search(email) > 0, email);

        List<String> actualFileNames = service.getAttachments(email);

        Collections.sort(actualFileNames);
        Collections.sort(fileNames);

        assertEquals("Actual list of attachments doesn't match to expected", fileNames, actualFileNames);
    }


    private EmailService service(User user) {
        if (user.getServiceType() == null) {
            throw new IllegalArgumentException("Service type is not set for user " + user);
        }

        if (user.getServiceType() == EmailServiceType.EXCHANGE) {
            return new ExchangeEmailService(user);
        }
        if (user.getServiceType() == EmailServiceType.GMAIL) {
            return new GmailEmailService(user);
        }

        throw new IllegalStateException("Service is not supported");
    }



}
