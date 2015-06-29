package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.Structure;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildTree {

    private Map<String,BuildOption> db;

    public BuildTree() {
        db = new HashMap<>();
        
        JsonValue structures = Assets.get("data/structures.json");
        for (JsonValue it : structures) {
            Structure s = new Structure(it);            
            db.put(s.getId(), s);
        }
        
        JsonValue ships = Assets.get("data/ships.json");        
        for (JsonValue it : ships) {
            JsonValue depends = it.get("depends");
            String[] dependList = new String[depends.size];
            for (int i = 0; i < depends.size; i++) {
                dependList[i] = depends.getString(i);
            }
            JsonValue actions = it.get("actions");
            Set<Unit.Action> actionSet = new HashSet<>();
            for (String action : actions.asStringArray()) {
                actionSet.add(Unit.Action.valueOf(action));
            }
            ShipType s = new ShipType(it.getString("id"), it.getString("label"), 
                it.getString("detail"), it.getInt("cost"), dependList,
                Range.valueOf(it.getString("range")), it.getInt("movementRange"), 
                it.getInt("scanRange"), actionSet);            
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
        // Do not allow duplicate structures
        Set<String> built = sector.getStructures();
        if ((option instanceof Structure) && built.contains(option.getId())) {
            return false;
        }   
        // Check if dependencies have been met
        for (String id : option.getDependencies()) {
            if (!built.contains(id)) {
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
        return (Structure)db.get(id);
    }
    
    public ShipType getShip(final String id) {
        return (ShipType)db.get(id);
    }
    
    public BuildOption getBuildOption(final String id) {
        return db.get(id);
    }
}