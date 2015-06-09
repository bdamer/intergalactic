package com.afqa123.intergalactic.data;

public class Structure {

    private final String id;
    private final String label;
    private final String detail;
    private final String[] dependencies;
    
    public Structure(String id, String label, String detail, String[] dependencies) {
        this.id = id;
        this.label = label;
        this.detail = detail;
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

    public String[] getDependencies() {
        return dependencies;
    }
    
    @Override
    public String toString() {
        return id;
    }
}