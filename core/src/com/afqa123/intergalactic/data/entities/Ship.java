package com.afqa123.intergalactic.data.entities;

import com.afqa123.intergalactic.data.model.ShipType;
import com.afqa123.intergalactic.data.entities.Faction;
import com.afqa123.intergalactic.data.entities.Unit;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Path.PathStep;
import com.afqa123.intergalactic.util.Pathfinder;

public class Ship implements Unit {

    private final ShipType type;    
    private final Faction owner;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    // Path from current coordinates to target
    private Path path; 
    // Movement points remaining this turn
    private float movementPoints;
    
    /**
     * Creates a new ship of a given type.
     * 
     * @param type The ship type.
     * @param owner The owner faction.
     */
    public Ship(ShipType type, Faction owner) {
        this.type = type;
        this.owner = owner;
        this.movementPoints = type.getMovementRange();
    }

    @Override
    public String getId() {
        return type.getId();
    }

    @Override
    public Faction getOwner() {
        return owner;
    }

    @Override
    public int getScanRange() {
        return type.getScanRange();
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
            Pathfinder finder = new AStarPathfinder(type.getRange(), owner.getMap());
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
            owner.getMap().explore(step.coordinate, type.getScanRange());
            movementPoints -= cost;
            path.pop();
        }
        
        if (path.isEmpty()) {
            path = null;
        }
    }
    
    @Override
    public void step() {
        movementPoints = type.getMovementRange();
    }

    @Override
    public boolean isReadyForStep() {
        // TODO: include other conditions (fortified, sleep, etc)
        return movementPoints < 1.0f;
    }
}