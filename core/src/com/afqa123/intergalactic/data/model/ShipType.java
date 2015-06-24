package com.afqa123.intergalactic.data.model;

import com.afqa123.intergalactic.data.entities.Range;
import com.afqa123.intergalactic.data.entities.Unit;
import java.util.Set;

public class ShipType implements BuildOption {
    
    private final String id;
    private final String label;
    private final String detail;
    private final int cost;
    private final String[] dependencies;
    private final Range range;
    private final int scanRange;
    private final int movementRange;
    private final Set<Unit.Action> actions;

    public ShipType(String id, String label, String detail, int cost, String[] dependencies, 
        Range range, int movementRange, int scanRange, Set<Unit.Action> actions) {
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
    
    public Set<Unit.Action> getActions() {
        return actions;
    }    
}