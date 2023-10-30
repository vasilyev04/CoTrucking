package com.kiparisov.cotrucking.model;

public class Message {
    private long timeStamp;
    private String userKey1;
    private String userKey2;
    private String ownerKey;
    private String text;

    public Message(){

    }

    public Message(long timeStamp, String ownerKey, String text, String userKey1, String userKey2) {
        this.timeStamp = timeStamp;
        this.ownerKey = ownerKey;
        this.text = text;
        this.userKey1 = userKey1;
        this.userKey2 = userKey2;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserKey1() {
        return userKey1;
    }

    public void setUserKey1(String userKey1) {
        this.userKey1 = userKey1;
    }

    public String getUserKey2() {
        return userKey2;
    }

    public void setUserKey2(String userKey2) {
        this.userKey2 = userKey2;
    }
}
