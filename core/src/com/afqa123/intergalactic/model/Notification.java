package com.afqa123.intergalactic.model;

public class Notification {

    public String title;
    public String message;
    
    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
}