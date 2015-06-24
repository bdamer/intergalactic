package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.data.entities.SectorStatus;
import com.afqa123.intergalactic.data.entities.Sector;
import com.afqa123.intergalactic.data.entities.Range;
import com.afqa123.intergalactic.data.entities.Faction;
import com.afqa123.intergalactic.math.HexCoordinate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactionMap {

    public interface ChangeListener {
        /**
         * Called whenever the faction map was updated.
         */
        void mapChanged();
    };
    
    public class SectorEntry {
        
        private Range range;
        private SectorStatus status;
        
        public SectorEntry() {
            range = null;
            status = SectorStatus.UNKNOWN;
        }
        
        public Range getRange() {
            return range;
        }
        
        public void setRange(Range range) {
            this.range = range;
        }

        /**
         * Sets range only if the new value is "better" than the previous value.
         * 
         * @param r The new range.
         */
        public void addRange(Range r) {
            if (range == null || range.ordinal() > r.ordinal()) {
                range = r;
            }
        }
        
        public SectorStatus getStatus() {
            return status;
        }

        public void setStatus(SectorStatus status) {
            this.status = status;
        }
        
        /**
         * Sets status only if the new value is "better" than the previous value.
         * 
         * @param s The new status.
         */
        public void addStatus(SectorStatus s) {
            if (status == null || status.ordinal() < s.ordinal()) {
                status = s;
            }
        }
    };

    private final Faction faction;
    // Sectors in this faction map. The first index represents the row, the second
    // the column.
    private final SectorEntry[][] map;
    private final int radius;
    private final Set<ChangeListener> listeners = new HashSet<>();
    
    public FactionMap(Faction faction, int radius) {
        this.faction = faction;
        this.radius = radius;
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        map = new SectorEntry[rows][];
        for (int i = 0; i < rows; i++) {
            map[i] = new SectorEntry[cols];
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new SectorEntry();
            }
            if (i < median) {
                cols++;
            } else if (i > median) {
                cols--;
            }
        }
    }
    
    public void update() {
        // Reset range
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j].range = null;
            }
        }
        
        // Then, set range based on faction's sectors
        List<Sector> sectors = faction.getSectors();
        for (Sector sector : sectors) {
            HexCoordinate coord = sector.getCoordinates();
            getSector(coord).range = Range.SHORT;

            // TODO: use custom range based on colony tech
            HexCoordinate[] shortRange = coord.getRing(1);
            for (HexCoordinate c : shortRange) {
                SectorEntry s = getSector(c);
                if (s != null) {
                    s.addRange(Range.SHORT);
                }
            }
            
            HexCoordinate[] mediumRange = coord.getRing(2);
            for (HexCoordinate c : mediumRange) {
                SectorEntry s = getSector(c);
                if (s != null) {
                    s.addRange(Range.MEDIUM);
                }
            }
            
            HexCoordinate[] longRange = coord.getRing(3);
            for (HexCoordinate c : longRange) {
                SectorEntry s = getSector(c);
                if (s != null) {
                    s.addRange(Range.LONG);
                }
            }
        }
        
        for (ChangeListener l : listeners) {
            l.mapChanged();
        }
    }
    
    public SectorEntry getSector(HexCoordinate c) {
        int row = c.y + radius - 1;
        int col = (c.y < 0 ? c.x + row : c.x + radius - 1);
        if (row < 0 || col < 0 || row > map.length - 1 || col > map[row].length - 1) {
            return null;
        } else {
            return map[row][col];        
        }
    }

    public SectorEntry[][] getSectors() {
        return map;
    }
    
    public void addColony(Sector colony) {
        HexCoordinate c = colony.getCoordinates();
        SectorEntry s = getSector(c);
        if (s != null) {
            s.status = SectorStatus.EXPLORED;
        }
        HexCoordinate[] shortRange = c.getRing(1);
        for (HexCoordinate cs : shortRange) {
            s = getSector(cs);
            if (s != null) {
                s.status = SectorStatus.KNOWN;
            }
        }
    }
    
    public void explore(HexCoordinate c, int radius) {
        // Mark this sector as explored
        SectorEntry s = getSector(c);
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
        update();
    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
}