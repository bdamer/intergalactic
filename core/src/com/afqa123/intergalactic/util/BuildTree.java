package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.entities.Range;
import com.afqa123.intergalactic.data.entities.Sector;
import com.afqa123.intergalactic.data.model.BuildOption;
import com.afqa123.intergalactic.data.model.ShipType;
import com.afqa123.intergalactic.data.model.Structure;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildTree {

    private static Map<String,BuildOption> db;

    private void initialize() {
        db = new HashMap<>();
        
        JsonValue structures = Assets.get("data/structures.json");
        for (JsonValue it : structures) {
            JsonValue depends = it.get("depends");
            String[] dependList = new String[depends.size];
            for (int i = 0; i < depends.size; i++) {
                dependList[i] = depends.getString(i);
            }
            Structure s = new Structure(it.getString("id"), it.getString("label"), 
                it.getString("detail"), it.getInt("cost"), dependList);            
            db.put(s.getId(), s);
        }
        
        JsonValue ships = Assets.get("data/ships.json");        
        for (JsonValue it : ships) {
            JsonValue depends = it.get("depends");
            String[] dependList = new String[depends.size];
            for (int i = 0; i < depends.size; i++) {
                dependList[i] = depends.getString(i);
            }
            ShipType s = new ShipType(it.getString("id"), it.getString("label"), 
                it.getString("detail"), it.getInt("cost"), dependList,
                Range.valueOf(it.getString("range")), it.getInt("movementRange"), it.getInt("scanRange"));            
            db.put(s.getId(), s);
        }
    }
    
    /**
     * Returns a list of structures and ships that are available to be build
     * in a sector.
     * 
     * @param sector The sector.
     * @return The list of build options.
     */
    public List<BuildOption> getBuildOptions(final Sector sector) {
        if (db == null) {
            initialize();
        }
        List<BuildOption> res = new ArrayList<>();
        for (BuildOption option : db.values()) {
            if (canBuild(sector, option)) {
                res.add(option);
            }
        }        
        return res;
    }
        
    /**
     * Checks if a structure or ship is available to be built in a sector.
     * 
     * @param sector The sector.
     * @param option The ship or structure.
     * @return True if the entry can be built.
     */
    public boolean canBuild(final Sector sector, final BuildOption option) {
        if (db == null) {
            initialize();
        }
        // Do not allow duplicate structures
        Set<Structure> built = sector.getStructures();
        if ((option instanceof Structure) && built.contains((Structure)option)) {
            return false;
        }   
        // Check if dependencies have been met
        for (String id : option.getDependencies()) {
            if (!built.contains((Structure)db.get(id))) {
                return false;
            }
        } 
        return true;
    }
    
    /**
     * Finds a {@code Structure} by id.
     * 
     * @param id The id.
     * @return The {@code Structure}.
     */
    public Structure getStructure(final String id) {
        if (db == null) {
            initialize();
        }
        return (Structure)db.get(id);
    }
    
    public ShipType getShip(final String id) {
        if (db == null) {
            initialize();
        }
        return (ShipType)db.get(id);
    }
}