package com.example.eco_admin.models;

import java.util.ArrayList;

public class Event {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String name;
    String date;
    String time;
    String Location;
    String description;
    String type;
    NGO ngo;

    ArrayList<RegisteredUser> registeredUsers;

    public ArrayList<RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(ArrayList<RegisteredUser> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NGO getNgo() {
        return ngo;
    }

    public void setNgo(NGO ngo) {
        this.ngo = ngo;
    }

    public Event(String name, String date, String time, String location, String description, String type, NGO ngo) {
        this.name = name;
        this.date = date;
        this.time = time;
        Location = location;
        this.description = description;
        this.type = type;
        this.ngo = ngo;
    }
}
