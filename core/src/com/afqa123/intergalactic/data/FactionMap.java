package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import java.util.List;

public class FactionMap {

    public enum Range {
        SHORT,
        MEDIUM,
        LONG
    };
    
    public enum Status {
        UNKNOWN,
        KNOWN,
        EXPLORED
    };
    
    public class SectorStatus {
        
        private Range range;
        private Status status;
        
        public SectorStatus() {
            range = null;
            status = Status.UNKNOWN;
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
        
        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    };

    private final Faction faction;
    // Sectors in this faction map. The first index represents the row, the second
    // the column.
    private final SectorStatus[][] map;
    private final int radius;

    public FactionMap(Faction faction, int radius) {
        this.faction = faction;
        this.radius = radius;
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        map = new SectorStatus[rows][];
        for (int i = 0; i < rows; i++) {
            map[i] = new SectorStatus[cols];
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new SectorStatus();
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
            List<HexCoordinate> shortRange = coord.getRing(1);
            for (HexCoordinate c : shortRange) {
                SectorStatus s = getSector(c);
                if (s != null) {
                    s.addRange(Range.SHORT);
                }
            }
            
            List<HexCoordinate> mediumRange = coord.getRing(2);
            for (HexCoordinate c : mediumRange) {
                SectorStatus s = getSector(c);
                if (s != null) {
                    s.addRange(Range.MEDIUM);
                }
            }
            
            List<HexCoordinate> longRange = coord.getRing(3);
            for (HexCoordinate c : longRange) {
                SectorStatus s = getSector(c);
                if (s != null) {
                    s.addRange(Range.LONG);
                }
            }
        }
    }
    
    public SectorStatus getSector(HexCoordinate c) {
        int row = c.y + radius - 1;
        int col = (c.y < 0 ? c.x + row : c.x + radius - 1);
        if (row < 0 || col < 0 || row > map.length - 1 || col > map[row].length - 1) {
            return null;
        } else {
            return map[row][col];        
        }
    }
    
    public void addHomeColony(Sector home) {
        HexCoordinate c = home.getCoordinates();
        SectorStatus s = getSector(c);
        if (s != null) {
            s.status = Status.EXPLORED;
        }
        List<HexCoordinate> shortRange = c.getRing(1);
        for (HexCoordinate cs : shortRange) {
            s = getSector(cs);
            if (s != null) {
                s.status = Status.KNOWN;
            }
        }
    }
}