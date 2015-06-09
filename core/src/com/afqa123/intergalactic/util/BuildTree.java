package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.data.Structure;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildTree {

    private static Map<String,Structure> db;

    private void initialize() {
        db = new HashMap<>();
        
        JsonValue structures = Assets.get("data/structures.json");
        for (JsonValue it : structures) {
            JsonValue depends = it.get("depends");
            String[] dependList = new String[depends.size];
            for (int i = 0; i < depends.size; i++) {
                dependList[i] = depends.getString(i);
            }
            Structure s = new Structure(it.getString("id"), it.getString("label"), it.getString("detail"), dependList);            
            db.put(s.getId(), s);
        }
        
        //JsonValue ships = Assets.get("data/ships.json");        
    }
    
    /**
     * Returns a list of structures and ships that are available to be build
     * in a sector.
     * 
     * @param sector The sector.
     * @return The list of queue entries.
     */
    public List<Structure> getBuildOptions(final Sector sector) {
        if (db == null) {
            initialize();
        }
        
        List<Structure> res = new ArrayList<>();
        for (Structure struct : db.values()) {
            if (canBuild(sector, struct)) {
                res.add(struct);
            }
        }        
        return res;
    }
    
    /**
     * Checks if a structure is available to be built in a sector.
     * 
     * @param sector The sector.
     * @param entry The entry.
     * @return True if the entry can be built.
     */
    public boolean canBuild(final Sector sector, final Structure struct) {
        if (db == null) {
            initialize();
        }

        Set<Structure> built = sector.getStructures();
        if (built.contains(struct)) {
            return false;
        }
        for (String id : struct.getDependencies()) {
            if (!built.contains(db.get(id))) {
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
        return db.get(id);
    }
}