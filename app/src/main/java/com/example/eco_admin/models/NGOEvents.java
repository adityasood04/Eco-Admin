package com.example.eco_admin.models;

import java.util.ArrayList;

public class NGOEvents {
    ArrayList<Event> eventsList;


    public NGOEvents() {
    }

    public ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(ArrayList<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public  void addEvent(Event event){
        if(eventsList == null) eventsList = new ArrayList<>();
        eventsList.add(event);
    }
}
