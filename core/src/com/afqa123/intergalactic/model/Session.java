package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.logic.EntityDatabase;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.logic.BuildTree;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Session containing the state of the game.
 */
public class Session implements Json.Serializable {
    
    private int turn;
    private int lastId;
    private Galaxy galaxy;
    private final EntityDatabase db;
    private final List<Unit> units;
    private final Map<String,Faction> factions;
    private final BuildTree buildTree;
    
    // TODO: add properties for difficulty, AI state, etc.
    
    Session() {
        db = new EntityDatabase();
        buildTree = new BuildTree(db);
        factions = new HashMap<>();
        units = new ArrayList<>();
    }
    
    public Session(Galaxy galaxy, Map<String,Faction> factions) {
        db = new EntityDatabase();
        buildTree = new BuildTree(db);
        this.galaxy = galaxy;
        this.factions = factions;
        this.units = new ArrayList<>();
    }
    
    public int getTurn() {
        return turn;
    }
    
    public void increaseTurns() {
        turn++;
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public Map<String,Faction> getFactions() {
        return factions;
    }

    public Faction getPlayer() {
        return factions.get(IntergalacticGame.PLAYER_FACTION);
    }
    
    public List<Unit> getUnits() {
        return units;
    }
 
    /**
     * Adds a new unit to the session. This will automatically assign an id
     * to the unit and add it to the faction's unit list.
     * 
     * @param unit The unit.
     */
    public void addUnit(Unit unit) {
        if (unit.getId() == null) {
            unit.setId(unit.getType() + (lastId++));
        }        
        units.add(unit);
        unit.getOwner().getUnits().add(unit);
        unit.getOwner().getMap().explore(unit.getCoordinates(), unit.getScanRange());
    }
    
    public void removeUnit(Unit unit) {
        unit.getOwner().getUnits().remove(unit);
        units.remove(unit);
    }
    
    public Unit findUnitInSector(HexCoordinate c) {
        for (Unit u : units) {
            if (u.getCoordinates().equals(c)) {
                return u;
            }
        }
        return null;
    }
    
    public Unit findUnit(String id) {
        for (Unit u : units) {
            if (id.equals(u.getId())) {
                return u;
            }
        }
        return null;
    }

    public EntityDatabase getDatabase() {
        return db;
    }
    
    public BuildTree getBuildTree() {
        return buildTree;
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("turn", turn);
        json.writeValue("lastId", lastId);        
        json.writeValue("galaxy", galaxy);
        json.writeValue("factions", factions.values());
        json.writeValue("units", units.toArray(new Unit[] { }));
    }

    @Override
    public void read(Json json, JsonValue jv) {
        turn = json.readValue("turn", Integer.class, jv);
        galaxy = json.readValue("galaxy", Galaxy.class, jv);
        Faction[] flist = json.readValue("factions", Faction[].class, jv);
        for (Faction f : flist) {
            factions.put(f.getName(), f);
        }
        Unit[] ulist = json.readValue("units", Unit[].class, jv);
        for (Unit u : ulist) {
            u.refresh(this);
            addUnit(u);
        }
    }
}