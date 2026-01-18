package com.deepdefender.finalyearproject.Fragment;


public class MessageModel {
    public String message;
    public String sender;
    public long time;
    public String imageBase64; // null for text messages
    public boolean deleted;


    public MessageModel() {}


    public MessageModel(String message, String sender, long time) {
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.imageBase64 = null;
        this.deleted = false;
    }


    public MessageModel(String sender, long time, String imageBase64) {
        this.message = "";
        this.sender = sender;
        this.time = time;
        this.imageBase64 = imageBase64;
        this.deleted = false;
    }
}