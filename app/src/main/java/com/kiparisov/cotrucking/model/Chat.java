package com.kiparisov.cotrucking.model;

public class Chat {
    private String userName;
    private String userAvatar;
    private long timestamp;
    private String companionKey;
    private String text;
    private String email;

    public Chat(String userName, String userAvatar, long timestamp, String text, String email, String companionKey) {
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.timestamp = timestamp;
        this.text = text;
        this.email = email;
        this.companionKey = companionKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanionKey() {
        return companionKey;
    }

    public void setCompanionKey(String companionKey) {
        this.companionKey = companionKey;
    }
}
