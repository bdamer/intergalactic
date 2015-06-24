package com.afqa123.intergalactic.data.model;

public class Structure implements BuildOption {

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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public boolean isUnique() {
        return true;
    }
    
    @Override
    public String[] getDependencies() {
        return dependencies;
    }
    
    @Override
    public String toString() {
        return id;
    }
}