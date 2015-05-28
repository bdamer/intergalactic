package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Path.PathStep;
import com.afqa123.intergalactic.util.Pathfinder;

public class Ship implements Unit {

    private final String id;
    private final Range range;
    private final Faction owner;
    private final int scanRange;
    private final int movementRange;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    // Path from current coordinates to target
    private Path path; 
    // Movement points remaining this turn
    private float movementPoints;
    
    public Ship(String id, Faction owner, Range range, int movementRange, int scanRange) {
        this.id = id;
        this.owner = owner;
        this.range = range;
        this.movementRange = movementRange;
        this.scanRange = scanRange;
        this.movementPoints = movementRange;
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
    public int getScanRange() {
        return scanRange;
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
            Pathfinder finder = new AStarPathfinder(range, owner.getMap());
            path = finder.findPath(coordinates, target);
        }
    }

    @Override
    public Path getPath() {
        return path;
    }
    
    @Override
    public void move() {
        if (path == null) {
            return;
        }        
        while (!path.isEmpty()) {
            PathStep step = path.peek();
            // TODO: determine cost of step
            float cost = 1.0f;
            if (movementPoints < cost) {
                break;
            }
            // TODO: check if target is valid
            // TODO: animate
            coordinates = step.coordinate;
            owner.getMap().explore(step.coordinate, scanRange);
            movementPoints -= cost;
            path.pop();
        }
        
        if (path.isEmpty()) {
            path = null;
        }
    }
    
    @Override
    public void step() {
        movementPoints = movementRange;
    }

    @Override
    public boolean isReadyForStep() {
        // TODO: include other conditions (fortified, sleep, etc)
        return movementPoints < 1.0f;
    }
}