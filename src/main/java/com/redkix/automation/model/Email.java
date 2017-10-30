package com.redkix.automation.model;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Email {

    private String recipient;
    private String subject;
    private String body;
    private List<String> ccRecipients = new ArrayList<>();
    private List<String> bccRecipients = new ArrayList<>();

    public Email() {
    }

    public Email(String recipient, String subject) {
        this.recipient = recipient;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public Email setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Email setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Email setBody(String body) {
        this.body = body;
        return this;
    }

    public List<String> getCcRecipients() {
        return ccRecipients;
    }

    public Email setCcRecipients(List<String> ccRecipients) {
        this.ccRecipients = ccRecipients;
        return this;
    }

    public List<String> getBccRecipients() {
        return bccRecipients;
    }

    public Email setBccRecipients(List<String> bccRecipients) {
        this.bccRecipients = bccRecipients;
        return this;
    }

    public void addCc(String email) {
        ccRecipients.add(email);
    }

    public void addBcc(String email) {
        bccRecipients.add(email);
    }

    public Email clearBody() {
        body = null;
        return this;
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<>();
        if (recipient != null) {
            parts.add("To='" +recipient + "'");
        }
        if (ccRecipients.size() > 0) {
            parts.add("Cc='" + ccRecipients.stream().collect(Collectors.joining(",")) + "'");
        }
        if (bccRecipients.size() > 0) {
            parts.add("Bcc='" + bccRecipients.stream().collect(Collectors.joining(",")) + "'");
        }
        if (subject != null) {
            parts.add("Subject='" + subject + "'");
        }
        if (body != null) {
            parts.add("Body='" + body + "'");
        }

        return "Email {" + parts.stream().collect(Collectors.joining(",")) + "}";
    }
}
