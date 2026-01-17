package com.deepdefender.finalyearproject.Fragment;

public class MessageModel {

    public String text;
    public String committee;
    public long timestamp;
    public boolean deleted;

    public MessageModel() { }

    public MessageModel(String text, String committee, long timestamp) {
        this.text = text;
        this.committee = committee;
        this.timestamp = timestamp;
        this.deleted = false;
    }
}
