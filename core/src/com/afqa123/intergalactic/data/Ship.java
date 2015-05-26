package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Pathfinder;

public class Ship {

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

    public String getId() {
        return id;
    }

    public Range getRange() {
        return range;
    }

    public Faction getOwner() {
        return owner;
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
        if (target == null) {
            path = null;
            this.target = null;
        } else {
            this.target = target;
            Pathfinder finder = new AStarPathfinder();
            path = finder.findPath(coordinates, target);            
        }
    }

    public Path getPath() {
        return path;
    }
    
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