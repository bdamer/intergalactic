package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Path.PathStep;
import com.afqa123.intergalactic.util.Pathfinder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Ship implements Unit, Json.Serializable {
    
    private ShipType type;
    private Faction owner;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    // Path from current coordinates to target
    private Path path; 
    // Movement points remaining this turn
    private float movementPoints;

    // TODO: fixme - only needed during deserialization
    private String typeName;
    private String ownerName;    
    
    Ship() {
        // required for serialization
    }
    
    /**
     * Creates a new ship of a given type.
     * 
     * @param type The ship type.
     * @param owner The owner faction.
     */
    public Ship(ShipType type, Faction owner) {
        this.type = type;
        this.owner = owner;
        this.ownerName = owner.getName();
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
        
    public void setOwner(Faction owner) {
        this.owner = owner;
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

    @Override
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

    @Override
    public boolean canPerformAction(Action action) {
        return type.getActions().contains(action);
    }

    @Override
    public void refresh(State state) {
        // needed to re-initialize after deserialization
        if (type == null) {
            type = state.getBuildTree().getShip(typeName);
        }
        if (owner == null) {
            owner = state.getFactions().get(ownerName);
        }
        if (target != null) {
            HexCoordinate newTarget = target;
            target = null;
            selectTarget(newTarget);
        }
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("type", type.getId());
        json.writeValue("owner", owner.getName());
        json.writeValue("coordinates", coordinates);
        json.writeValue("target", target);
        json.writeValue("movementPoints", movementPoints);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        typeName = json.readValue("type", String.class, jv);
        ownerName = json.readValue("owner", String.class, jv);
        coordinates = json.readValue("coordinates", HexCoordinate.class, jv);
        target = json.readValue("target", HexCoordinate.class, jv);
        movementPoints = json.readValue("movementPoints", Float.class, jv);        
    }
}