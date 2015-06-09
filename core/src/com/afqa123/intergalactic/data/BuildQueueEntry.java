package com.afqa123.intergalactic.data;

public class BuildQueueEntry {

    private final String id;
    private float cost;

    public BuildQueueEntry(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        return id;
    }
}