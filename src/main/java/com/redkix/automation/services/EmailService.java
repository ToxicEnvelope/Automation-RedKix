package com.redkix.automation.services;


import com.redkix.automation.model.Email;
import com.redkix.automation.model.Folder;

import java.util.List;

public interface EmailService {

    int search(Email email);
    int searchInFolder(Email email, Folder folder);
    void send(Email email);
    void markAsRead(Email email);
    void markAsUnread(Email email);
    boolean isRead(Email email);
    void reply(Email originalEmail, String replyBody, boolean toAll);
    List<String> getAttachments(Email email);



}
