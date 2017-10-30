package com.redkix.automation.model;


import java.util.Optional;

public enum Folder {

    DELETED_ITEMS("Deleted Items"),
    JUNK_EMAIL("Junk Email"),
    SENT_EMAIL("Sent Item"),
    ARCHIVE("Archive"),
    SCHEDULED("Scheduled", ARCHIVE),
    FOLLOW_UP("Follow up", ARCHIVE);

    Folder(String name, Folder... parent) {
        this.name = name;

        if (parent != null && parent.length > 0) {
            this.parent = Optional.of(parent[0]);
        }
    }

    public String getName() {
        return name;
    }

    public Optional<Folder> getParent() {
        return parent;
    }

    private String name;
    private Optional<Folder> parent = Optional.empty();
}
