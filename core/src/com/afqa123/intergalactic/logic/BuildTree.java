package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.logic.EntityDatabase;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.StructureType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildTree {

    private final Map<String,BuildOption> db = new HashMap<>();

    public BuildTree(EntityDatabase edb) {
        // Populate internal database
        for (ShipType ship : edb.getShips()) {
            db.put(ship.getId(), ship);
        }
        for (StructureType struct : edb.getStructures()) {
            db.put(struct.getId(), struct);
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
        if ((option instanceof StructureType) && built.contains(option.getId())) {
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
        
    public BuildOption getBuildOption(final String id) {
        return db.get(id);
    }
}