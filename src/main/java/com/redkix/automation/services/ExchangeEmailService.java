package com.redkix.automation.services;


import com.redkix.automation.model.Email;
import com.redkix.automation.model.Folder;
import com.redkix.automation.model.User;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExchangeEmailService implements EmailService {

    private User user;

    public ExchangeEmailService(User user) {
        this.user = user;
    }

    @Override
    public void markAsRead(Email email) {
        changeIsRead(email, true);
    }

    @Override
    public void markAsUnread(Email email) {
        changeIsRead(email, false);
    }

    @Override
    public boolean isRead(Email email) {
        try {
            return findLastEmailMessage(email).getIsRead();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(Email email) {
        try {
            EmailMessage emailMessage = new EmailMessage(service());
            emailMessage.getToRecipients().add(email.getRecipient());
            emailMessage.setSubject(email.getSubject());

            for (String bcc : email.getBccRecipients()) {
                emailMessage.getBccRecipients().add(bcc);
            }
            for (String cc : email.getCcRecipients()) {
                emailMessage.getCcRecipients().add(cc);
            }

            emailMessage.setBody(MessageBody.getMessageBodyFromText(email.getBody()));
            emailMessage.send();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reply(Email originalEmail, String body, boolean toAll) {
        try {
            EmailMessage emailMessages = findLastEmailMessage(originalEmail);
            emailMessages.reply(new MessageBody(body), toAll);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAttachments(Email email) {
        try {
            EmailMessage emailMessage = findLastEmailMessage(email);
            emailMessage = EmailMessage.bind(service(), emailMessage.getId(),
                    new PropertySet(BasePropertySet.IdOnly, ItemSchema.Attachments, ItemSchema.HasAttachments));

            if (!emailMessage.getHasAttachments()) {
                return new ArrayList<>();
            }

            return emailMessage.getAttachments().getItems().stream().
                    map(Attachment::getName).
                    collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int search(Email email) {
        try {
            return findEmailMessages(email).size();
        } catch (Exception|AssertionError e) {
            return 0;
        }
    }

    @Override
    public int searchInFolder(Email email, Folder folder) {

        WellKnownFolderName folderName = convertToWellKnownFolderName(folder);

        try {
            return findEmailMessages(email, folderName).size();
        } catch (Exception|AssertionError e) {
            return 0;
        }
    }

    private WellKnownFolderName convertToWellKnownFolderName(Folder folder) {
        Map<Folder, WellKnownFolderName> mapping = new HashMap<>();
        mapping.put(Folder.SENT_EMAIL, WellKnownFolderName.SentItems);
        mapping.put(Folder.DELETED_ITEMS, WellKnownFolderName.DeletedItems);
        mapping.put(Folder.ARCHIVE, WellKnownFolderName.ArchiveRoot);

        if (!mapping.containsKey(folder)) {
            throw new IllegalArgumentException("Folder " + folder + " is not supported yet");
        }

        return mapping.get(folder);
    }

    private ExchangeService service() {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

        try {
            service.setUrl(new URI("https://outlook.office365.com/ews/exchange.asmx"));
        } catch (URISyntaxException ignore) {}

        ExchangeCredentials credentials = new WebCredentials(user.getEmail(), user.getPassword());
        service.setCredentials(credentials);

        return service;
    }

    private List<EmailMessage> findEmailMessages(Email email, WellKnownFolderName... folders) throws Exception {
        List<SearchFilter> searchFilterCollection = new ArrayList<>();

        if (email.getSubject() != null) {
            searchFilterCollection.add(new SearchFilter.ContainsSubstring(ItemSchema.Subject, email.getSubject()));
        }

        SearchFilter searchFilter = new SearchFilter.SearchFilterCollection(LogicalOperator.And,
                searchFilterCollection.toArray(new SearchFilter[searchFilterCollection.size()]));

        ItemView view = new ItemView(10);
        view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, ItemSchema.Subject, ItemSchema.DateTimeReceived,
                EmailMessageSchema.IsRead));

        if (folders == null || folders.length < 1) {
            folders = new WellKnownFolderName[]{WellKnownFolderName.Inbox};
        }

        List<EmailMessage> emailResults = new ArrayList<>();

        for (WellKnownFolderName folder : folders) {
            FindItemsResults<Item> results = service().findItems(folder, searchFilter, view);

            emailResults.addAll(results.getItems().stream().
                    filter(r -> r instanceof EmailMessage).
                    map(r -> (EmailMessage) r).
                    collect(Collectors.toList()));
        }

        if (emailResults.size() < 1) {
            throw new AssertionError("No emails found by search criteria");
        }

        return emailResults;
    }

    private void changeIsRead(Email email, boolean isRead) {
        try {
            EmailMessage emailMessage = findLastEmailMessage(email);
            emailMessage.setIsRead(isRead);
            emailMessage.update(ConflictResolutionMode.AlwaysOverwrite);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private EmailMessage findLastEmailMessage(Email email) throws Exception {

        List<EmailMessage> emailMessages = findEmailMessages(email);
        if (emailMessages.size() == 0) {
            throw new RuntimeException("No emails with subject " +  email.getSubject() + " found");
        }

        return emailMessages.get(emailMessages.size() - 1);
    }

}
