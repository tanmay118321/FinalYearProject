package com.deepdefender.finalyearproject;

public class ComplaintModel {

    public String id;
    public String subject;
    public String details;
    public String category;
    public String status;
    public long timestamp;

    public ComplaintModel() { }

    public ComplaintModel(String id, String subject, String details,
                          String category, String status, long timestamp) {
        this.id = id;
        this.subject = subject;
        this.details = details;
        this.category = category;
        this.status = status;
        this.timestamp = timestamp;
    }
}
