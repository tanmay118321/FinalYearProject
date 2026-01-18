package com.deepdefender.finalyearproject;

public class AttendanceModel {

    public String name, room, status, time, profileImageBase64;

    public AttendanceModel() {}

    public AttendanceModel(String n, String r, String s, String t, String img) {
        name = n;
        room = r;
        status = s;
        time = t;
        profileImageBase64 = img;
    }
}
