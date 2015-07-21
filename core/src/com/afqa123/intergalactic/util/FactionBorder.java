package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.math.Edge;
import com.afqa123.intergalactic.math.Hex;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.FactionMap;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.Range;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class FactionBorder {

    private static final float BORDER_OFFSET = 0.05f;
    private final List<Edge> innerBorder = new ArrayList<>();
    private final List<Edge> outerBorder = new ArrayList<>();
    private final FactionMap map;
    
    public FactionBorder(FactionMap map) {
        this.map = map;
        computeBorders();
    }
    
    /**
     * Computes the inner and outer border of this map. An inner border exists 
     * between a SHORT and a LONG range sector, whereas an outer border exists
     * between a LONG range sector and one without a range.
     */
    private void computeBorders() {
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
            FactionMapSector sector = map.getSector(cur);
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
            for (HexCoordinate.Direction d : HexCoordinate.Direction.values()) {
                HexCoordinate n = neighbors[d.ordinal()];
                FactionMapSector neighbor = map.getSector(n);
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
    
    public List<Edge> getInnerBorder() {
        return innerBorder;
    }

    public List<Edge> getOuterBorder() {
        return outerBorder;
    }
}