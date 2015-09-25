package com.afqa123.intergalactic.model;

public class ShipType implements UnitType, BuildOption {
    
    public enum Action {
        ATTACK, BUILD_STATION, COLONIZE, INTERCEPT
    };
    
    private String id;
    private String label;
    private String detail;
    // Build cost
    private int cost;
    // Build dependencies
    private String[] dependencies;
    // Movement range (short or long)
    private Range range;
    // Scan range
    private int scanRange;
    // Number of movement points per turn
    private int movementPoints;
    // Base attack value
    private float attack;
    // Base defense value
    private float defense;
    // Max crew size
    private float crewSize;
    // Max shield power
    private float shieldCapacity;
    // Shield recharge rate
    private float shieldRecharge;
    // Available actions
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

    public int getMovementPoints() {
        return movementPoints;
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

    public float getCrewSize() {
        return crewSize;
    }

    public float getShieldCapacity() {
        return shieldCapacity;
    }

    public float getShieldRecharge() {
        return shieldRecharge;
    }
    
    public Action[] getActions() {
        return actions;
    }    
}