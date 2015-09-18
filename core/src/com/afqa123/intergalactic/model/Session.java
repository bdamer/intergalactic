package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.asset.Strings;
import com.afqa123.intergalactic.logic.EntityDatabase;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.logic.CombatSimulator;
import com.afqa123.intergalactic.logic.CombatSimulator.CombatResult;
import static com.afqa123.intergalactic.logic.CombatSimulator.CombatResult.DEFEAT;
import static com.afqa123.intergalactic.logic.CombatSimulator.CombatResult.DRAW;
import static com.afqa123.intergalactic.logic.CombatSimulator.CombatResult.VICTORY;
import com.afqa123.intergalactic.logic.UnitListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    private final Queue<Notification> notifications;
    private final Set<UnitListener> listeners = new HashSet<>();
    
    Session() {
        db = new EntityDatabase();
        buildTree = new BuildTree(db);
        factions = new HashMap<>();
        units = new ArrayList<>();
        notifications = new LinkedList<>();
    }
    
    public Session(Galaxy galaxy, Map<String,Faction> factions) {
        db = new EntityDatabase();
        buildTree = new BuildTree(db);
        this.galaxy = galaxy;
        this.factions = factions;
        units = new ArrayList<>();
        notifications = new LinkedList<>();
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
        return factions.get(Faction.PLAYER_FACTION);
    }
    
    public Faction getPirates() {
        return factions.get(Faction.PIRATE_FACTION);
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
        for (UnitListener l : listeners) {
            l.unitCreated(ship);
        }
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
        for (UnitListener l : listeners) {
            l.unitCreated(station);
        }
        return station;
    }
        
    public void destroyStation(Station station) {
        destroyUnit(station);
    }

    public void destroyUnit(Unit unit) {
        if (unit instanceof Ship) {
            Ship ship = (Ship)unit;
            unit.getOwner().getShips().remove((Ship)unit);
        }
        units.remove(unit);
        for (UnitListener l : listeners) {
            l.unitDestroyed(unit);
        }
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
        
    public void trigger(GameEvent e, Object... arguments) {
        Sector sector;
        Faction faction;
        switch (e) {
            case FIRST_VISIT_TO_SECTOR:
                sector = (Sector)arguments[0];
                faction = (Faction)arguments[1];
                if (!faction.isPirates()) {
                    randomSectorEvent(sector, faction);
                }
                break;
            case SECTOR_GROWTH:
                sector = (Sector)arguments[0];
                faction = factions.get(sector.getOwner());
                if (faction.isPlayer()) {
                    notifications.add(new Notification(Strings.get("DIALOG_POP_GROWTH"),
                        String.format(Strings.get("MSG_POP_GROWTH"), sector.getName()), 
                            sector.getCoordinates()));
                }
                break;
            case SECTOR_STARVATION:
                sector = (Sector)arguments[0];
                faction = factions.get(sector.getOwner());
                if (faction.isPlayer()) {
                    notifications.add(new Notification(Strings.get("DIALOG_POP_SHORT"), 
                        String.format(Strings.get("MSG_POP_SHORT"), sector.getName()), 
                            sector.getCoordinates()));
                }
                break;
        }
    }
    
    private void randomSectorEvent(Sector sector, Faction faction) {
        sector.setFlag(Sector.FLAG_EXPLORED);
        if (Math.random() < Settings.<Double>get("eventOnSectorExploration")) {
            // TODO: implement effect
            // queue up UI event
            if (faction.isPlayer()) {
                notifications.add(new Notification(Strings.get("DIALOG_SECTOR_EXPLORED"), 
                    Strings.get("MSG_SECTOR_EXPLORED"), sector.getCoordinates()));
            }
        }                
    }
    
    public CombatResult simulateCombat(Unit attacker, Unit defender) {
        CombatSimulator sim = new CombatSimulator();
        CombatResult res = sim.simulate(attacker, defender);
        switch (res) {
            case VICTORY:
                if (defender.getHealth() <= 0.0f) {
                    destroyUnit(defender);
                }
                break;
            case DEFEAT:
                if (attacker.getHealth() <= 0.0f) {
                    destroyUnit(attacker);
                }
                break;
            case DRAW:
                break;
        }
        return res;
    }
    
    public Notification pollNotifications() {
        return notifications.poll();
    }
    
    public boolean hasNotifications() {
        return !notifications.isEmpty();
    }
    
    public void addUnitListener(UnitListener l) {
        listeners.add(l);
    }
    
    public void removeUnitListener(UnitListener l) {
        listeners.remove(l);
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