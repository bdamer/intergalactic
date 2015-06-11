package com.afqa123.intergalactic.data;

public class Structure {

    private final String id;
    private final String label;
    private final String detail;
    private final int cost;
    private final String[] dependencies;
    
    public Structure(String id, String label, String detail, int cost, String[] dependencies) {
        this.id = id;
        this.label = label;
        this.detail = detail;
        this.cost = cost;
        this.dependencies = dependencies;
    }

    public String getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }

    public String getDetail() {
        return detail;
    }

    public int getCost() {
        return cost;
    }

    public String[] getDependencies() {
        return dependencies;
    }
    
    @Override
    public String toString() {
        return id;
    }
}