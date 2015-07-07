package com.afqa123.intergalactic.model;

public class ShipType implements UnitType, BuildOption {
    
    public enum Action {
        ATTACK, BUILD_STATION, COLONIZE, INTERCEPT
    };
    
    private String id;
    private String label;
    private String detail;
    private int cost;
    private String[] dependencies;
    private Range range;
    private int scanRange;
    private int movementRange;
    private Action[] actions;

    ShipType() {
        
    }
    
    public ShipType(String id, String label, String detail, int cost, String[] dependencies, 
        Range range, int movementRange, int scanRange, Action[] actions) {
        this.id = id;
        this.label = label;
        this.detail = detail;
        this.cost = cost;
        this.dependencies = dependencies;
        this.range = range;
        this.movementRange = movementRange;
        this.scanRange = scanRange;
        this.actions = actions;
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
        return false;
    }

    @Override
    public String[] getDependencies() {
        return dependencies;
    }
    
    public Range getRange() {
        return range;
    }

    public int getMovementRange() {
        return movementRange;
    }
    
    public int getScanRange() {
        return scanRange;
    }
    
    public Action[] getActions() {
        return actions;
    }    
}