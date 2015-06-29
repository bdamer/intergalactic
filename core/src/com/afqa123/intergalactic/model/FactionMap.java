package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashSet;
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
    
    public FactionMap(int radius) {
        this.radius = radius;
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        map = new FactionMapSector[rows][];
        for (int i = 0; i < rows; i++) {
            map[i] = new FactionMapSector[cols];
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new FactionMapSector();
            }
            if (i < median) {
                cols++;
            } else if (i > median) {
                cols--;
            }
        }
    }

    private void updateSector(HexCoordinate coord) {
        getSector(coord).setRange(Range.SHORT);

        // TODO: use custom range based on colony tech
        HexCoordinate[] shortRange = coord.getRing(1);
        for (HexCoordinate c : shortRange) {
            FactionMapSector s = getSector(c);
            if (s != null) {
                s.addRange(Range.SHORT);
            }
        }

        HexCoordinate[] mediumRange = coord.getRing(2);
        for (HexCoordinate c : mediumRange) {
            FactionMapSector s = getSector(c);
            if (s != null) {
                s.addRange(Range.MEDIUM);
            }
        }

        HexCoordinate[] longRange = coord.getRing(3);
        for (HexCoordinate c : longRange) {
            FactionMapSector s = getSector(c);
            if (s != null) {
                s.addRange(Range.LONG);
            }
        }

        for (FactionMap.ChangeListener l : listeners) {
            l.mapChanged();
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
    
    public void addColony(HexCoordinate c) {
        FactionMapSector s = getSector(c);
        if (s != null) {
            s.setStatus(SectorStatus.EXPLORED);
        }
        HexCoordinate[] shortRange = c.getRing(1);
        for (HexCoordinate cs : shortRange) {
            s = getSector(cs);
            if (s != null) {
                s.setStatus(SectorStatus.KNOWN);
            }
        }
        updateSector(c);
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