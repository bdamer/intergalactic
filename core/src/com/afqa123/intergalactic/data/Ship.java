package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;

public class Ship {

    private final String id;
    private final Range range;
    private Faction owner;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    
    // TODO: add type information
    
    public Ship(String id, Range range) {
        this.id = id;
        this.range = range;
    }

    public String getId() {
        return id;
    }

    public Range getRange() {
        return range;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }

    public HexCoordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(HexCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    public HexCoordinate getTarget() {
        return target;
    }

    public void setTarget(HexCoordinate target) {
        this.target = target;
    }
}