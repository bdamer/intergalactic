package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.StationType;
import com.afqa123.intergalactic.model.StructureType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityDatabase {

    private final Map<String,StructureType> structures = new HashMap<>();
    private final Map<String,ShipType> ships = new HashMap<>();
    private final Map<String,StationType> stations = new HashMap<>();

    public EntityDatabase() {
        Json json = new Json();
        
        String raw = Assets.get("data/structures.json");
        StructureType[] structList = json.fromJson(StructureType[].class, raw);
        Gdx.app.debug(BuildTree.class.toString(), String.format("Loaded %d structures.", structList.length));
        for (StructureType s : structList) {
            structures.put(s.getId(), s);
        }
        
        raw = Assets.get("data/ships.json");        
        ShipType[] shipsList = json.fromJson(ShipType[].class, raw);
        Gdx.app.debug(BuildTree.class.toString(), String.format("Loaded %d ships.", shipsList.length));
        for (ShipType s : shipsList) {
            ships.put(s.getId(), s);
        }

        raw = Assets.get("data/stations.json");        
        StationType[] stationList = json.fromJson(StationType[].class, raw);
        Gdx.app.debug(BuildTree.class.toString(), String.format("Loaded %d stations.", stationList.length));
        for (StationType s : stationList) {
            stations.put(s.getId(), s);
        }
    }    
    
    public Collection<StructureType> getStructures() {
        return structures.values();
    }
    
    public Collection<ShipType> getShips() {
        return ships.values();
    }
    
    public Collection<StationType> getStations() {
        return stations.values();
    }
    
    /**
     * Finds a {@code Structure} by id.
     * 
     * @param id The id.
     * @return The {@code Structure}.
     */
    public StructureType getStructure(final String id) {
        return structures.get(id);
    }
    
    public ShipType getShip(final String id) {
        return ships.get(id);
    }

    public StationType getStation(final String id) {
        return stations.get(id);
    }
}