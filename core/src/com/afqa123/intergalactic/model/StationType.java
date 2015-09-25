package com.afqa123.intergalactic.model;

public class StationType implements UnitType {
    
    private String id;
    private String label;
    private int scanRange;
    private int cost;
    private float defense;
    private float health;
    private float shieldRecharge;
    
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

    public int getCost() {
        return cost;
    }
    
    public float getDefense() {
        return defense;
    }

    public float getHealth() {
        return health;
    }
    
    public float getShieldRecharge() {
        return shieldRecharge;
    }
}
