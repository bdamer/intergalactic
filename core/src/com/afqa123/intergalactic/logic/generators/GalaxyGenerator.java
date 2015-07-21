package com.afqa123.intergalactic.logic.generators;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.StarType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;

public class GalaxyGenerator {

    // Prime number use to generate randomized sector name order
    private static final int RANDOM_STEP = 19;
    private final String[] sectorNames;
    private int lastNameIdx;

    public GalaxyGenerator() {
        sectorNames = ((JsonValue)Assets.get("data/sectors.json")).asStringArray();
        lastNameIdx = (int)(Math.random() * sectorNames.length);        
    }
    
    public Galaxy generateRandomGalaxy(int radius) {
        Gdx.app.log(Galaxy.class.getName(), "Building irregular galaxy.");
        Galaxy res = new Galaxy(radius);
        Sector[][] sectors = res.getSectors();
        for (int y = 0; y < sectors.length; y++) {
            for (int x = 0; x < sectors[y].length; x++) {
                // TODO: revisit and come up with proper map generation mechanism
                boolean hasStar = (Math.random() < 0.1);
                if (hasStar) {
                    Vector2 axial = res.offsetToAxial(x, y);
                    Sector sector = new Sector(sectorNames[(lastNameIdx += RANDOM_STEP) % sectorNames.length], 
                            new HexCoordinate(axial), StarType.values()[(int)(Math.random() * 5)]);
                    res.addStarSystem(sector);
                }            
            }
        }
        return res;
    }
    
    public Galaxy generateSpiralGalaxy(int radius) {
        Gdx.app.log(Galaxy.class.getName(), "Building spiral galaxy.");
        Galaxy res = new Galaxy(radius);
        
        final float MIN_ANGLE = 0.4f;  // min increase in angle per step
        final float MAX_ANGLE = 1.5f;  // max increase in angle per step        
        // TODO: HEIGHT * width at mid point should give us max radius
        final float RADIUS = 20.0f;     // radius of the spiral 
        // TODO: base on size of galaxy [tiny, small, medium, large, huge]
        final float NUM_ROT = 3.0f;     // number of circles in this spiral
        
        float ratio = 0.0f;
        float angle = 0.0f;
        while (ratio < 1.0) {
            // determine the ratio of the current angle to the max angle 
            ratio = angle / (NUM_ROT * (float)(2.0 * Math.PI));
            Vector3 pos = new Vector3((float)Math.cos(angle) * RADIUS * ratio,
                                      0.0f,
                                      (float)Math.sin(angle) * RADIUS * ratio);
            HexCoordinate coord = new HexCoordinate(pos);

            // only create star sector if there currently is none at these coordinates.
            if (isValidCoordinateForSector(res, coord)) {
                Sector sector = new Sector(sectorNames[(lastNameIdx += RANDOM_STEP) % sectorNames.length], 
                        coord, StarType.values()[(int)(Math.random() * 5)]);
                res.addStarSystem(sector);
            }
            
            // Angle changes based on inverse ratio, so that we end up with an
            // event distribution (i.e., the increase in angle gets smaller
            // as the radius increases).
            angle += MIN_ANGLE + Math.random() * (1.0f - ratio) * (MAX_ANGLE - MIN_ANGLE);
        }
        
        Gdx.app.debug(GalaxyGenerator.class.getName(), "Built galaxy with " + res.getCount() + " sectors.");
        
        return res;
    }
    
    private boolean isValidCoordinateForSector(Galaxy galaxy, HexCoordinate coord) {
        Sector[][] sectors = galaxy.getSectors();
        Vector2 offset = galaxy.axialToOffset((int)coord.x, (int)coord.y);
        // Bounds check
        if (offset.y < 0.0f || (int)offset.y >= sectors.length ||
            offset.x < 0.0f || (int)offset.x >= sectors[(int)offset.y].length) {
            return false;
        } else if (galaxy.getSectors()[(int)offset.y][(int)offset.x].getType() != null) {
            return false;
        }
        HexCoordinate[] neighbors = coord.getRing(1);
        for (HexCoordinate n : neighbors) {
            offset = galaxy.axialToOffset((int)n.x, (int)n.y);
            // Bounds check
            if (offset.y < 0.0f || (int)offset.y >= sectors.length ||
                offset.x < 0.0f || (int)offset.x >= sectors[(int)offset.y].length) {
                continue;
            } else if (galaxy.getSectors()[(int)offset.y][(int)offset.x].getType() != null) {
                return false;
            }            
        }
        return true;
    }
    
    private Sector createStarSector(Galaxy galaxy, HexCoordinate coord) {
        String name = sectorNames[(lastNameIdx += RANDOM_STEP) % sectorNames.length];
        StarType type = StarType.values()[(int)(Math.random() * 5)];
        Sector s = new Sector(name, coord, type);
        galaxy.getStarSystems().add(s);
        return s;
    }    
}