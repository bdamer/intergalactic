package com.afqa123.intergalactic.model;

public class StructureType implements BuildOption {

    private String id;
    private String label;
    private String detail;
    // TODO: replace with structured data
    private String provides;
    private int cost;
    private String[] dependencies;
    
    StructureType() {
        
    }
    
    public StructureType(String id, String label, String detail, int cost, String[] dependencies) {
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