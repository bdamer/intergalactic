package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.UnitType.Action;
import com.afqa123.intergalactic.util.AStarPathfinder;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Path.PathStep;
import com.afqa123.intergalactic.util.Pathfinder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Ship implements Unit, Json.Serializable {
    
    private String id;
    private ShipType type;
    private Faction owner;
    private HexCoordinate coordinates;
    private HexCoordinate target;
    // Path from current coordinates to target
    private Path path; 
    // Movement points remaining this turn
    private float movementPoints;
    private boolean fortified;

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
        this.movementPoints = type.getMovementRange();
        this.owner = owner;
        this.ownerName = owner.getName();
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
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
    public float getMovementPoints() {
        return movementPoints;
    }
    
    public Range getRange() {
        return type.getRange();
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

    public void fortify() {
        this.fortified = true;
        this.path = null;
        this.target = null;
    }
    
    @Override
    public void move() {
        if (path == null) {
            return;
        }        
        while (!path.isEmpty()) {
            PathStep step = path.peek();
            if (movementPoints < step.cost) {
                break;
            }
            // TODO: check if target is valid
            // TODO: animate
            coordinates = step.coordinate;
            owner.getMap().explore(step.coordinate, type.getScanRange());
            movementPoints -= step.cost;
            path.pop();
        }
        
        if (path.isEmpty()) {
            path = null;
            target = null;
        }
    }
    
    @Override
    public void step() {
        movementPoints = type.getMovementRange();
    }

    @Override
    public boolean isReadyForStep() {
        return (fortified || movementPoints < 1.0f);
    }

    @Override
    public boolean canPerformAction(Action action) {
        for (Action a : type.getActions()) {
            if (a == action) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean colonizeSector(Session session) {
        if (!canPerformAction(Action.COLONIZE))
            return false;

        Sector s = session.getGalaxy().getSector(coordinates);
        if (!s.canColonize())
            return false;

        owner.addColony(s);
        session.removeUnit(this);
        return true;
    }
        
    // TODO: this step should take a few turns (maybe just flag outpost unit as
    // "under construction"?)
    @Override
    public boolean buildStation(Session session) {
        if (!canPerformAction(Action.BUILD_STATION)) {
            return false;
        }
        
        Sector s = session.getGalaxy().getSector(coordinates);
        if (!s.canBuildOutpost())
            return false;
        
        // For now, station type must match ship type
        StationType stationType = session.getDatabase().getStation(type.getId());
        Station station = owner.addStation(s, stationType);                            
        // Station replace builder unit
        session.removeUnit(this);
        session.addUnit(station);                            
        return true;
    }

    @Override
    public void wake() {
        fortified = false;
    }

    @Override
    public void refresh(Session state) {
        // needed to re-initialize after deserialization
        if (type == null) {
            type = state.getDatabase().getShip(typeName);
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
        json.writeValue("id", id);
        json.writeValue("type", type.getId());
        json.writeValue("owner", owner.getName());
        json.writeValue("coordinates", coordinates);
        json.writeValue("target", target);
        json.writeValue("movementPoints", movementPoints);
        json.writeValue("fortified", fortified);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        id = json.readValue("id", String.class, jv);
        typeName = json.readValue("type", String.class, jv);
        ownerName = json.readValue("owner", String.class, jv);
        coordinates = json.readValue("coordinates", HexCoordinate.class, jv);
        target = json.readValue("target", HexCoordinate.class, jv);
        movementPoints = json.readValue("movementPoints", Float.class, jv);        
        fortified = json.readValue("fortified", Boolean.class, jv);
    }
}