package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.Structure;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildTree {

    private final Map<String,BuildOption> db = new HashMap<>();

    public BuildTree() {
        Json json = new Json();
        
        String raw = Assets.get("data/structures.json");
        Structure[] structures = json.fromJson(Structure[].class, raw);
        Gdx.app.debug(BuildTree.class.toString(), String.format("Loaded %d structures.", structures.length));
        for (Structure s : structures) {
            db.put(s.getId(), s);
        }
        
        raw = Assets.get("data/ships.json");        
        ShipType[] ships = json.fromJson(ShipType[].class, raw);
        Gdx.app.debug(BuildTree.class.toString(), String.format("Loaded %d ships.", ships.length));
        for (ShipType s : ships) {
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