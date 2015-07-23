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
    private float attack;
    private float defense;
    private float health;
    private Action[] actions;

    ShipType() {
        
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

    public float getAttack() {
        return attack;
    }

    public float getDefense() {
        return defense;
    }

    public float getHealth() {
        return health;
    }
    
    public Action[] getActions() {
        return actions;
    }    
}