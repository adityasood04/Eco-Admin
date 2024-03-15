package com.example.eco_admin.models;

import java.util.ArrayList;

public class NGO {
    String uid;
    String name;
    String registrationNo;
    String address;
    long contactNo;
    String website;
    String email;
    ArrayList<PreviousEvent> previousEvents;
    public NGO() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<PreviousEvent> getPreviousEvents() {
        return previousEvents;
    }

    public void setPreviousEvents(ArrayList<PreviousEvent> previousEvents) {
        this.previousEvents = previousEvents;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getContactNo() {
        return contactNo;
    }

    public void setContactNo(long contactNo) {
        this.contactNo = contactNo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
