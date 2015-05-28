package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Pathfinder;

public class Ship implements Unit {

    private final String id;
    private final Range range;
    private final Faction owner;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    // Path from current coordinates to target
    private Path path; 
    
    // TODO: add type information
    
    public Ship(String id, Range range, Faction owner) {
        this.id = id;
        this.range = range;
        this.owner = owner;
    }

    @Override
    public String getId() {
        return id;
    }

    public Range getRange() {
        return range;
    }

    @Override
    public Faction getOwner() {
        return owner;
    }

    @Override
    public HexCoordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(HexCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    public HexCoordinate getTarget() {
        return target;
    }

    @Override
    public void selectTarget(HexCoordinate target) {
        if (target == null) {
            path = null;
            this.target = null;
        } else if (!target.equals(this.target)) {
            this.target = target;
            Pathfinder finder = new AStarPathfinder();
            path = finder.findPath(coordinates, target);            
        }
    }

    @Override
    public Path getPath() {
        return path;
    }
    
    @Override
    public void move() {
        if (path != null) {
            path.pop();
            coordinates = path.peek();
            
            // TODO: use variable line of sight
            owner.getMap().explore(coordinates, 2);
            
            // path always contains the start and target, so once we are down
            // to one entry, we are at the target
            if (path.size() < 2) {
                path = null;
            }
        }
    }
}