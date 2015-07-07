package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.model.FactionMap;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.util.Path.PathStep;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * A* implementation of a {@code Pathfinder}.
 */
public class AStarPathfinder implements Pathfinder {

    // Base cost to move from one tile to another
    private final static float BASE_COST = 1.0f;
    // Cost for moving through a sector with unit in it
    private final static float OCCUPIED_COST = 15.0f;
    
    private class PathNode {
        
        final HexCoordinate coord;
        final PathNode parent;
        // cost to move to this node from previous node
        final float cost;
        // cost to get to this node from start of path
        final float cumulativeCost;
    
        public PathNode(HexCoordinate coord, PathNode parent) {
            this.coord = coord;
            this.parent = parent;
            // estimated cost from here to target
            float estimatedCost = coord.getDistance(to);
            if (parent != null) {
                // TODO: cost needs to include sector information from map
                cost = BASE_COST + (session.findUnitInSector(coord) != null ? OCCUPIED_COST : 0.0f);
                cumulativeCost = parent.cost + cost + estimatedCost;
            } else {
                cost = 0.0f;
                cumulativeCost = estimatedCost;
            }            
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PathNode) {
                return coord.equals(((PathNode)obj).coord);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return coord.hashCode();
        }
    };

    private final Set<PathNode> visited;
    private final PriorityList<PathNode> candidates;
    private final Range validRange;
    private final Session session;
    private final FactionMap map;
    private HexCoordinate to;
    
    public AStarPathfinder(Range validRange, Session session, FactionMap map) {
        this.validRange = validRange;
        this.session = session;
        this.map = map;
        candidates = new PriorityList<>(new Comparator<PathNode>() {
            @Override
            public int compare(PathNode o1, PathNode o2) {
                return (int)(o1.cumulativeCost - o2.cumulativeCost);
            }
        });
        visited = new HashSet<>();
    }
    
    @Override
    public Path findPath(HexCoordinate from, HexCoordinate to) {
        if (from.equals(to)) {
            return null;
        }
        
        this.to = to;
        visited.clear();
        candidates.clear();

        // Add starting node
        PathNode node = new PathNode(from, null);
        visited.add(node);
        addNeighbors(node);

        Path res = null;
        while (candidates.size() > 0) {
            // get cheapest element
            PathNode cur = candidates.pop();
            // mark as visited
            visited.add(cur);
            // We're at our target
            if (cur.coord.equals(to)) {
                // build up path
                res = new Path();
                while (cur.parent != null) {
                    res.push(new PathStep(cur.coord, cur.cost, false));
                    cur = cur.parent;
                }
                break;
            } else {
                addNeighbors(cur);
            }
        }        
        
        return res;
    }
    
    private void addNeighbors(PathNode parent) {
        HexCoordinate[] neighbors = parent.coord.getRing(1);
        for (HexCoordinate c : neighbors) {
            FactionMapSector sector = map.getSector(c);
            if (sector == null) {
                continue; // skip invalid coordinates
            }
            Range range = sector.getRange();
            if (range == null || range.ordinal() > validRange.ordinal()) {
                continue; // skip invalid coordinates
            }
            PathNode node = new PathNode(c, parent);
            if (visited.contains(node)) {
                continue; // skip nodes already on the path
            }
            // Check if we have a prior entry for these coordiantes
            int idx = candidates.indexOf(node);
            if (idx > -1) {
                PathNode prior = candidates.remove(idx);
                node = node.cumulativeCost < prior.cumulativeCost ? node : prior;
            }
            candidates.add(node);
        }
    }    
}