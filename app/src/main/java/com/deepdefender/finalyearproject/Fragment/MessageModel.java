package com.deepdefender.finalyearproject.Fragment;

public class MessageModel {

    public String text, sender;
    public long timestamp;
    public boolean deleted;

    public MessageModel() {}

    public MessageModel(String text, String sender, long timestamp) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
        this.deleted = false;
    }
}
