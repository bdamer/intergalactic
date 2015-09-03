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
     * Factory method for creating new ships.
     * 
     * @param type The ship type.
     * @param coordinates The initial coordinates.
     * @param faction The owner faction.
     * @return A new {@code Ship} instance.
     */
    public Ship createShip(ShipType type, HexCoordinate coordinates, Faction faction) {
        Ship ship = new Ship(type.getId() + lastId++, type, coordinates, faction);
        units.add(ship);        
        faction.getShips().add(ship);
        faction.getMap().explore(coordinates, type.getScanRange());
        return ship;
    }
    
    public void destroyShip(Ship ship) {
        destroyUnit(ship);
    }

    /**
     * Factory method for creating new stations.
     * 
     * @param type The station type.
     * @param coordinates The initial coordinates.
     * @param faction The owner faction.
     * @return A new {@code Station} instance.
     */
    public Station createStation(StationType type, HexCoordinate coordinates, Faction faction) {
        Station station = new Station(type.getId() + lastId++, type, coordinates, faction);
        units.add(station);
        galaxy.getSector(coordinates).setOwner(faction.getName());
        return station;
    }
        
    public void destroyStation(Station station) {
        destroyUnit(station);
    }

    public void destroyUnit(Unit unit) {
        if (unit instanceof Ship) {
            unit.getOwner().getShips().remove((Ship)unit);
        }
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
        // initialize modifiers
        for (Sector s : galaxy.getStarSystems()) {
            s.updateModifiers(this);
        }
        Faction[] flist = json.readValue("factions", Faction[].class, jv);
        for (Faction f : flist) {
            factions.put(f.getName(), f);
        }
        Unit[] ulist = json.readValue("units", Unit[].class, jv);
        for (Unit u : ulist) {
            u.refresh(this);
            if (u instanceof Ship) {
                u.getOwner().getShips().add((Ship)u);
            }
            units.add(u);
        }
    }
}