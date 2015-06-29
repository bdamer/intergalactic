package com.afqa123.intergalactic.model;

public class BuildQueueEntry {

    private String id;
    private String label;
    private float cost;

    BuildQueueEntry() {
        // required for serialization
    }
    
    /**
     * Creates a new {@code BuildQueueEntry}.
     * 
     * @param id The build option id.
     * @param initialCost The initial cost of the build option.
     */
    public BuildQueueEntry(String id, String label, float initialCost) {
        this.id = id;
        this.label = label;
        this.cost = initialCost;
    }

    public String getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        return label;
    }
}