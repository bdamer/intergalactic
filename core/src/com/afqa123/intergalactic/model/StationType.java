package com.afqa123.intergalactic.model;

public class StationType implements UnitType {
    
    private String id;
    private String label;
    private int scanRange;
    private Action[] actions;

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

    @Override
    public Action[] getActions() {
        return actions;
    }
}
