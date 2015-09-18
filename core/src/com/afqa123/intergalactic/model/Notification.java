package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;

public class Notification {

    private final String title;
    private final String message;
    private final HexCoordinate focus;
    
    public Notification(String title, String message, HexCoordinate focus) {
        this.title = title;
        this.message = message;
        this.focus = focus;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public HexCoordinate getFocus() {
        return focus;
    }
}