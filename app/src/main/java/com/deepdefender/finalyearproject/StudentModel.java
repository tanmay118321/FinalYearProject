package com.deepdefender.finalyearproject;

public class StudentModel {

    // Firestore document id
    public String uid;

    // Basic info
    public String name;
    public String email;
    public String room;
    public String studentId;

    // Role & status
    public String role;        // "student"
    public String status;      // "active" | "inactive"

    // Payment
    public boolean paymentDue; // true / false

    // Required empty constructor for Firestore
    public StudentModel() {}
}
