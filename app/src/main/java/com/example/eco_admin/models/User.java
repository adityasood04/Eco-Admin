package com.example.eco_admin.models;

public class User {
    String name;
    String uid;
    String email;
    String password;
    long mobileNo;

    public User() {
    }

    public User(String name, String uid, String email, String password, long mobileNo) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.mobileNo = mobileNo;
    }
}
