package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.logic.CombatSimulator;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.ShipType.Action;
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
    private float health;
    private boolean fortified;

    // TODO: fixme - only needed during deserialization
    private String typeName;
    private String ownerName;    
    
    Ship() {
        // required for serialization
    }
    
    /**
     * Creates a new ship of a given type. Unit should only be created using 
     * the factory methods provided by {@code Session}.
     * 
     * @param id The ship's id
     * @param type The ship type.
     * @param coordinates The initial coordinates.
     * @param owner The owner faction.
     */
    Ship(String id, ShipType type, HexCoordinate coordinates, Faction owner) {
        this.id = id;
        this.type = type;
        this.movementPoints = type.getMovementRange();
        this.coordinates = coordinates;
        this.owner = owner;
        this.ownerName = owner.getName();
        this.health = type.getHealth();
    }
    
    @Override
    public String getId() {
        return id;
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
    
    public float getMovementPoints() {
        return movementPoints;
    }

    @Override
    public float getBaseAttack() {
        return type.getAttack() * getPower();
    }
    
    @Override
    public float getBaseDefense() {
        return type.getDefense() * getPower();
    }
    
    @Override
    public float getPower() {
        float power = health / type.getHealth();
        return Math.max(power, 0.1f);
    }
    
    @Override
    public void applyDamage(float damage) {
        health -= damage;
    }
    
    @Override
    public float getHealth() {
        return health;
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

    public HexCoordinate getTarget() {
        return target;
    }

    public void selectTarget(Session session, HexCoordinate target) {
        if (target == null || target.equals(coordinates)) {
            path = null;
            this.target = null;
        } else if (!target.equals(this.target)) {
            this.target = target;
            Unit anotherUnit = session.findUnitInSector(target);
            if (anotherUnit != null && canAttack(anotherUnit)) {
                // Create attack path
                path = new Path();
                path.add(new PathStep(target, 1.0f, true));
            } else {
                Pathfinder finder = new AStarPathfinder(type.getRange(), session, owner);
                path = finder.findPath(coordinates, target);
            }
        }
    }

    public Path getPath() {
        return path;
    }

    /**
     * Moves this ship towards its target.
     * 
     * @param session The game session.
     * @return True if the ship was moved, false if it could not moved or was 
     * blocked.
     */
    public boolean move(Session session) {
        if (path == null) {
            return false;
        }
        boolean res = true;
        while (!path.isEmpty()) {
            PathStep step = path.peek();
            if (movementPoints < step.cost) {
                break;
            }
            path.pop();
            
            // Check if next sector is occupied by another unit
            Unit u = session.findUnitInSector(step.coordinate);
            if (u != null) {
                // TODO: combat should probably be handled somewhere else...
                if (step.attack) {
                    // once we attack, no more movement is possible
                    movementPoints = 0.0f;
                    // Attack logic
                    CombatSimulator sim = new CombatSimulator();
                    switch (sim.simulate(this, u)) {
                        case VICTORY:
                            if (u.getHealth() <= 0.0f) {
                                session.destroyUnit(u);
                            }
                            break;
                        case DEFEAT:
                            if (getHealth() <= 0.0f) {
                                session.destroyUnit(this);
                            }
                            return true;
                        case DRAW:
                            return false;
                    }
                } else {
                    // allow unit to pass through a sector with units as long
                    // as it ends its turn somewhere else
                    boolean canPass = false;
                    if (path.size() > 0) {
                        PathStep nextStep = path.peek();
                        Unit nextu = session.findUnitInSector(nextStep.coordinate);
                        if (nextu == null && movementPoints >= (step.cost + nextStep.cost)) {
                            canPass = true;
                        }
                    }
                    if (!canPass) {
                        // abandon current path if we cannot move even though we
                        // are at full movement points
                        if (movementPoints == type.getMovementRange()) {
                            path.clear();  
                        }
                        res = false;
                        break;
                    }
                }
            }
            coordinates = step.coordinate;
            owner.getMap().explore(step.coordinate, type.getScanRange());
            movementPoints -= step.cost;
        }
        
        if (path.isEmpty()) {
            path = null;
            target = null;
        }
        return res;
    }
    
    public boolean isReadyForUpdate() {
        return (fortified || movementPoints < 1.0f);
    }

    @Override
    public void update(Session session) {
        movementPoints = type.getMovementRange();
    }

    /**
     * Checks if this unit can attack another unit.
     * 
     * @param unit The unit to attack.
     * @return True if unit can attack, otherwise false.
     */
    public boolean canAttack(Unit unit) {
        // Cannot attack your own units
        if (unit.getOwner().equals(owner)) {
            return false;
        // For now, combat is only possible between adjacent units
        } else if (coordinates.getDistance(unit.getCoordinates()) > 1 || movementPoints < 1.0f) {
            return false;
        } else {
            // TODO: include other criteria
            return true;
        }
    }

    public boolean canPerformAction(Action action) {
        for (Action a : type.getActions()) {
            if (a == action) {
                return true;
            }
        }
        return false;
    }

    /**
     * If possible will colonize the current sector. If successful, this action
     * will remove this unit from the session.
     * 
     * @param session The game session.
     * @return True if sector was colonized, otherwise false.
     */
    public boolean colonizeSector(Session session) {
        if (!canPerformAction(Action.COLONIZE))
            return false;

        Sector s = session.getGalaxy().getSector(coordinates);
        if (!s.canColonize())
            return false;

        owner.addColony(s);
        session.destroyUnit(this);
        return true;
    }
        
    /**
     * If possible will build a station in the current sector. If successful,
     * this action will remove this unit from the session.
     * 
     * @param session The game session.
     * @return True if stations was build, otherwise false.
     */
    public boolean buildStation(Session session) {
        if (!canPerformAction(Action.BUILD_STATION)) {
            return false;
        }
        
        Sector s = session.getGalaxy().getSector(coordinates);
        if (!s.canBuildOutpost())
            return false;
        
        // For now, station type must match ship type
        StationType stationType = session.getDatabase().getStation(type.getId());
        session.createStation(stationType, coordinates, owner);
        // Station replaces builder unit
        session.destroyUnit(this);
        return true;
    }
    
    public void fortify() {
        this.fortified = true;
        this.path = null;
        this.target = null;
    }    

    public void wake() {
        fortified = false;
    }

    @Override
    public void refresh(Session session) {
        // needed to re-initialize after deserialization
        if (type == null) {
            type = session.getDatabase().getShip(typeName);
        }
        if (owner == null) {
            owner = session.getFactions().get(ownerName);
        }
        if (target != null) {
            HexCoordinate newTarget = target;
            target = null;
            selectTarget(session, newTarget);
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
        json.writeValue("health", health);
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
        health = json.readValue("health", Float.class, jv);
    }
}