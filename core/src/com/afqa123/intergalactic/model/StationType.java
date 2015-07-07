package com.afqa123.intergalactic.model;

public class StationType implements UnitType {
    
    private String id;
    private String label;
    private int scanRange;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getScanRange() {
        return scanRange;
    }
}
