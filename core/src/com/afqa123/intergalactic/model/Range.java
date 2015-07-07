package com.afqa123.intergalactic.model;

/**
 * Range defines movement of ships within the galaxy.
 */
public enum Range {

    SHORT(2),
    LONG(4);
    
    private final int distance;
    
    private Range(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }
};