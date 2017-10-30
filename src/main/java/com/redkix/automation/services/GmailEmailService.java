package com.redkix.automation.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.redkix.automation.helper.EmailHelper;
import com.redkix.automation.helper.UserHelper;
import com.redkix.automation.model.Email;
import com.redkix.automation.model.Folder;
import com.redkix.automation.model.User;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.api.services.gmail.GmailScopes.GMAIL_METADATA;

public class GmailEmailService implements EmailService {

    /** Application name. */
    private static final String APPLICATION_NAME = "rdx-app-automation";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.dir"), "src/main/resources/credentials");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    private static final String apiUser = "me";
    private User user;

    public GmailEmailService(User user) {
        this.user = user;
    }

    private static final List<String> SCOPES = GmailScopes.all().stream().
            filter(s -> !s.equals(GMAIL_METADATA)).
            collect(Collectors.toList());

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GmailEmailService.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize(user.getEmail());
//        System.out.println(
//                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public Gmail service() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public int search(Email email) {
        try {
            return findMessages(email).size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int searchInFolder(Email email, Folder folder) {

        try {
            List<String> labelIds = Arrays.asList(getLabelId(folder));

            List<Message> messages = service().users().messages().
                    list(apiUser).
                    setQ("subject:" + email.getSubject()).
                    setLabelIds(labelIds).
                    execute().getMessages();

            return messages.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(Email email) {
        try {
            MimeMessage mimeMessage = convertToMimeMessage(email);
            Message message = convertToMessage(mimeMessage);
            service().users().messages().send(apiUser, message).execute();
        }
        catch (MessagingException|IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markAsRead(Email email) {
        try {
            Message message = findLastEmailMessage(email);

            ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(Arrays.asList("UNREAD"));

            service().users().messages().modify(apiUser, message.getId(), mods).execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markAsUnread(Email email) {
        try {
            Message message = findLastEmailMessage(email);

            ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(Arrays.asList("UNREAD"));

            service().users().messages().modify(apiUser, message.getId(), mods).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRead(Email email) {
        try {
            Message message = findLastEmailMessage(email);

            return message.getLabelIds().stream().noneMatch(l -> l.equals("UNREAD"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reply(Email originalEmail, String replyBody, boolean toAll) {
        try {
            List<Message> messages = service().users().messages().
                    list(apiUser).
                    setQ("subject:" + originalEmail.getSubject()).
                    execute().getMessages();

            Message message = messages.get(messages.size() - 1);

            MimeMessage mimeMessage = getMimeMessage(message.getId());
            javax.mail.Message mimeReply = mimeMessage.reply(toAll);
            mimeReply.setText(replyBody);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mimeReply.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

            Message replyMessage = new Message();
            replyMessage.setRaw(encodedEmail);

            service().users().messages().send(apiUser, replyMessage).execute();
        } catch (IOException|MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAttachments(Email email) {

        try {
            Message message = findLastEmailMessage(email);

            List<MessagePart> parts = message.getPayload().getParts();
            List<String> attachments = new ArrayList<>();

            for (MessagePart part : parts) {
                if (part.getFilename() != null && part.getFilename().length() > 0) {
                    String filename = part.getFilename();
                    attachments.add(filename);
                }
            }

            return attachments;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Message convertToMessage(MimeMessage mimeEmail) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        mimeEmail.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private MimeMessage convertToMimeMessage(Email email) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage mimeEmail = new MimeMessage(session);

        mimeEmail.setFrom(new InternetAddress(user.getEmail()));
        mimeEmail.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(email.getRecipient()));

        for (String cc : email.getCcRecipients()) {
            mimeEmail.addRecipient(javax.mail.Message.RecipientType.CC,
                    new InternetAddress(cc));
        }

        for (String bcc : email.getBccRecipients()) {
            mimeEmail.addRecipient(javax.mail.Message.RecipientType.BCC,
                    new InternetAddress(bcc));
        }

        mimeEmail.setSubject(email.getSubject());
        mimeEmail.setText(email.getBody());

        return mimeEmail;
    }

    private List<Message> findMessages(Email email) throws IOException {
        List<Message> messages = service().users().messages().
                list(apiUser).
                setQ("subject:" + email.getSubject()).
                execute().getMessages();

        if (messages == null || messages.size() == 0) {
            return new ArrayList<>();
        }

        List<Message> foundMessages = new ArrayList<>();

        for (Message message : messages) {
            Message m = service().users().messages().
                    get(apiUser, message.getId()).
                    execute();

            foundMessages.add(m);
        }

        return foundMessages;
    }

    private MimeMessage getMimeMessage(String messageId)
            throws IOException, MessagingException {
        Message message = service().users().messages().get(apiUser, messageId).setFormat("raw").execute();

        Base64 base64Url = new Base64(true);
        byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
        return email;
    }

    private String getLabelId(Folder folder) {
        Map<Folder, String> mapping = new HashMap<>();
        mapping.put(Folder.SENT_EMAIL, "SENT");
        mapping.put(Folder.DELETED_ITEMS, "TRASH");

        if (!mapping.containsKey(folder)) {
            throw new IllegalArgumentException("Folder " + folder + " is not supported yet");
        }

        return mapping.get(folder);
    }

    private Message findLastEmailMessage(Email email) throws IOException {

        List<Message> messages = findMessages(email);
        if (messages.size() == 0) {
            throw new RuntimeException("No emails with subject " +  email.getSubject() + " found");
        }

        return messages.get(messages.size() - 1);
    }
}
