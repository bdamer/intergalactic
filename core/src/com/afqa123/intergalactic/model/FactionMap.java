package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactionMap implements Json.Serializable {

    public interface ChangeListener {
        /**
         * Called whenever the faction map was updated.
         */
        void mapChanged();
    };

    // Sectors in this faction map. The first index represents the row, the second
    // the column.
    private FactionMapSector[][] map;
    private int radius;
    private final Set<ChangeListener> listeners = new HashSet<>();
    
    FactionMap() {
        // required for serialization
    }
    
    public FactionMap(Galaxy galaxy) {
        this.radius = galaxy.getRadius();
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        map = new FactionMapSector[rows][];
        for (int i = 0; i < rows; i++) {
            map[i] = new FactionMapSector[cols];
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new FactionMapSector(new HexCoordinate(galaxy.offsetToAxial(j, i)));
            }
            if (i < median) {
                cols++;
            } else if (i >= median) {
                cols--;
            }
        }        
    }
    
    public FactionMapSector getSector(HexCoordinate c) {
        int row = c.y + radius - 1;
        int col = (c.y < 0 ? c.x + row : c.x + radius - 1);
        if (row < 0 || col < 0 || row > map.length - 1 || col > map[row].length - 1) {
            return null;
        } else {
            return map[row][col];        
        }
    }

    public FactionMapSector[][] getSectors() {
        return map;
    }

    public List<FactionMapSector> findSectors(SectorStatus status, Range range) {
        List<FactionMapSector> res = new ArrayList<>();
        for (FactionMapSector[] row : map) {
            for (FactionMapSector s : row) {
                if (s.getStatus().ordinal() >= status.ordinal() &&
                    (s.getRange() != null && s.getRange().ordinal() <= range.ordinal())) {
                    res.add(s);
                }
            }
        }
        return res;
    }
    
    /**
     * Increases the range of player movement on the faction map.
     * 
     * @param c The coordinate to use as a new range center (ex: colony or outpost)
     */
    public void addRange(HexCoordinate coord) {        
        getSector(coord).setRange(Range.SHORT);

        // TODO: use custom range based on colony / outpost tech
        for (int i = 0; i < Range.SHORT.getDistance(); i++) {
            HexCoordinate[] shortRange = coord.getRing(i + 1);
            for (HexCoordinate c : shortRange) {
                FactionMapSector s = getSector(c);
                if (s != null) {
                    s.addRange(Range.SHORT);
                }
            }            
        }

        for (int i = Range.SHORT.getDistance(); i < Range.LONG.getDistance(); i++) {
            HexCoordinate[] shortRange = coord.getRing(i + 1);
            for (HexCoordinate c : shortRange) {
                FactionMapSector s = getSector(c);
                if (s != null) {
                    s.addRange(Range.LONG);
                }
            }            
        }
        // Explore and notify map listeners
        explore(coord, 1);
    }
        
    public void explore(HexCoordinate c, int radius) {
        // Mark this sector as explored
        FactionMapSector s = getSector(c);
        s.addStatus(SectorStatus.EXPLORED);        
        // Mark neighbors as known
        for (int i = 1; i <= radius; i++) {
            HexCoordinate[] ring = c.getRing(i);
            for (HexCoordinate hc : ring) {
                s = getSector(hc);
                if (s != null) {
                    s.addStatus(SectorStatus.KNOWN);
                }
            }
        }    
        // TODO: potential optimization - only call listeners if map was actually changed
        for (FactionMap.ChangeListener l : listeners) {
            l.mapChanged();
        }
    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    @Override
    public void write(Json json) {
        json.writeValue("map", map);
        json.writeValue("radius", radius);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        map = json.readValue("map", FactionMapSector[][].class, jv);
        radius = json.readValue("radius", Integer.class, jv);
    }
}