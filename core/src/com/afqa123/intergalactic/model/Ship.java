package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.logic.CombatSimulator.CombatResult;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.ShipType.Action;
import com.afqa123.intergalactic.logic.pathfinding.AStarPathfinder;
import com.afqa123.intergalactic.logic.pathfinding.Path;
import com.afqa123.intergalactic.logic.pathfinding.Path.PathStep;
import com.afqa123.intergalactic.logic.pathfinding.Pathfinder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;

public class Ship extends Entity implements Unit, Json.Serializable {
    
    public static final String FLAG_FORTIFIED = "fortified";
    public static final String FLAG_DESTROYED = "destroyed";
    
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
            if (anotherUnit != null && canAttack(anotherUnit) && canMoveTo(target)) {
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
    
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    /**
     * Moves this ship towards its target.
     * 
     * @param session The game session.
     * @return True if the ship was moved, false if it could not moved or was 
     * blocked.
     */
    public boolean move(Session session) {
        if (!hasPath()) {
            return false;
        }
        
        boolean res = true;
        while (!path.isEmpty()) {            
            // Check movment cost
            PathStep step = path.peek();
            if (movementPoints < step.cost) {
                break;
            }
            path.pop();
            
            // Check if next sector is occupied by another unit
            Unit u = session.findUnitInSector(step.coordinate);
            if (u != null) {
                if (step.attack) {
                    // once we attack, no more movement is possible
                    movementPoints = 0.0f;
                    // in case of defeat or draw, movement is done
                    if (session.simulateCombat(this, u) != CombatResult.VICTORY) {
                        path = null;
                        target = null;
                        return true;
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
            
            // If we make it this far, we can move to the next sector
            // TODO: revisit to synchronize ship movement
            coordinates = step.coordinate;
            movementPoints -= step.cost;
            Sector sector = session.getGalaxy().getSector(step.coordinate);
            owner.getMap().explore(step.coordinate, type.getScanRange());
            // First visitor to a new sector triggers discovery event
            if (sector.getType() != null && !sector.getFlag(Sector.FLAG_EXPLORED)) {
                session.trigger(GameEvent.FIRST_VISIT_TO_SECTOR, sector, owner);
                return false; // return false to stop movement
            }
        }
        
        if (path.isEmpty()) {
            path = null;
            target = null;
        }
        return res;
    }
    
    public boolean isReadyForUpdate() {
        return (getFlag(FLAG_FORTIFIED) || movementPoints < 1.0f);
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
    
    public boolean canMoveTo(HexCoordinate coord) {
        FactionMapSector sector = owner.getMap().getSector(coord);
        return (sector.getRange().ordinal() <= getRange().ordinal());
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
        s.colonize(session, ownerName);
        // explore player map around new colony
        owner.getMap().addRange(s.getCoordinates());
        session.destroyUnit(this);
        session.trigger(GameEvent.SECTOR_COLONIZED, s);
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
        setFlag(FLAG_FORTIFIED);
        this.path = null;
        this.target = null;
    }    

    public void wake() {
        unsetFlag(FLAG_FORTIFIED);
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
        json.writeValue("health", health);
        json.writeValue("flags", flags);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        id = json.readValue("id", String.class, jv);
        typeName = json.readValue("type", String.class, jv);
        ownerName = json.readValue("owner", String.class, jv);
        coordinates = json.readValue("coordinates", HexCoordinate.class, jv);
        target = json.readValue("target", HexCoordinate.class, jv);
        movementPoints = json.readValue("movementPoints", Float.class, jv);        
        health = json.readValue("health", Float.class, jv);
        flags.putAll(json.readValue("flags", HashMap.class, jv));
    }
}