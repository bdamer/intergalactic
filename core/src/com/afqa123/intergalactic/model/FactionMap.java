package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.Edge;
import com.afqa123.intergalactic.math.Hex;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.math.HexCoordinate.Direction;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    private final List<Edge> innerBorder = new ArrayList<>();
    private final List<Edge> outerBorder = new ArrayList<>();
    
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

    public List<Edge> getInnerBorder() {
        return innerBorder;
    }

    public List<Edge> getOuterBorder() {
        return outerBorder;
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
        computeBorders();
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
        for (FactionMap.ChangeListener l : listeners) {
            l.mapChanged();
        }
    }
    
    // TODO: move border computation into seperate class that stores faction borders
    // and provides border conflict resolution mechanisms
    private static final float BORDER_OFFSET = 0.05f;
    
    /**
     * Computes the inner and outer border of this map. An inner border exists 
     * between a SHORT and a LONG range sector, whereas an outer border exists
     * between a LONG range sector and one without a range.
     */
    public void computeBorders() {
        Set<HexCoordinate> visited = new HashSet<>();
        Queue<HexCoordinate> candidates = new LinkedList<>();
        innerBorder.clear();
        outerBorder.clear();
        
        candidates.add(HexCoordinate.ORIGIN);
        while (!candidates.isEmpty()) {
            HexCoordinate cur = candidates.remove();
            if (visited.contains(cur)) {
                continue;
            }
            visited.add(cur);

            // Check if this is a valid coordinate
            FactionMapSector sector = getSector(cur);
            if (sector == null) {
                continue;
            }

            // Add neighbors to list of candidates
            HexCoordinate[] neighbors = cur.getRing(1);
            candidates.addAll(Arrays.asList(neighbors));

            // Check if this sector is within faction range
            Range sectorRange = sector.getRange();
            if (sectorRange == null) {
                continue;
            }
            
            Vector3 origin = cur.toWorld();
            // check each neighbor sector
            for (Direction d : Direction.values()) {
                HexCoordinate n = neighbors[d.ordinal()];
                FactionMapSector neighbor = getSector(n);
                // if neighbor sector off the map or has a different range 
                // than the current sector, we've found a border
                boolean isOuterBorder = (neighbor == null || (sectorRange == Range.LONG && neighbor.getRange() == null));
                boolean isInnerBorder = (sectorRange == Range.SHORT && (neighbor == null || neighbor.getRange() == Range.LONG));
                if (isOuterBorder || isInnerBorder) {
                    Edge edge = null;
                    switch (d) {
                        case NORTH_EAST:
                            edge = new Edge(new Vector3(origin.x, origin.y, origin.z - Hex.SIZE + BORDER_OFFSET), 
                                new Vector3(origin.x + Hex.HEIGHT - BORDER_OFFSET, origin.y, origin.z - Hex.HALF_SIZE + BORDER_OFFSET));
                            break;
                        case EAST:
                            edge = new Edge(new Vector3(origin.x + Hex.HEIGHT - BORDER_OFFSET, origin.y, origin.z - Hex.HALF_SIZE + BORDER_OFFSET), 
                                new Vector3(origin.x + Hex.HEIGHT - BORDER_OFFSET, origin.y, origin.z + Hex.HALF_SIZE - BORDER_OFFSET));
                            break;
                        case SOUTH_EAST:
                            edge = new Edge(new Vector3(origin.x + Hex.HEIGHT - BORDER_OFFSET, origin.y, origin.z + Hex.HALF_SIZE - BORDER_OFFSET), 
                                new Vector3(origin.x, origin.y, origin.z + Hex.SIZE - BORDER_OFFSET));
                            break;
                        case SOUTH_WEST:
                            edge = new Edge(new Vector3(origin.x, origin.y, origin.z + Hex.SIZE - BORDER_OFFSET), 
                                new Vector3(origin.x - Hex.HEIGHT + BORDER_OFFSET, origin.y, origin.z + Hex.HALF_SIZE - BORDER_OFFSET));
                            break;
                        case WEST:
                            edge = new Edge(new Vector3(origin.x - Hex.HEIGHT + BORDER_OFFSET, origin.y, origin.z + Hex.HALF_SIZE - BORDER_OFFSET),
                                new Vector3(origin.x - Hex.HEIGHT + BORDER_OFFSET, origin.y, origin.z - Hex.HALF_SIZE + BORDER_OFFSET));
                            break;
                        case NORTH_WEST:
                            edge = new Edge(new Vector3(origin.x - Hex.HEIGHT + BORDER_OFFSET, origin.y, origin.z - Hex.HALF_SIZE + BORDER_OFFSET),
                                new Vector3(origin.x, origin.y, origin.z - Hex.SIZE + BORDER_OFFSET));
                            break;                                    
                    }
                    if (isInnerBorder) {
                        innerBorder.add(edge);
                    }
                    if (isOuterBorder) {
                        outerBorder.add(edge);
                    }
                }
            }
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