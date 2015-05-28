package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.data.FactionMap;
import com.afqa123.intergalactic.data.Range;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.Path.PathStep;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * A* implementation of a {@code Pathfinder}.
 */
public class AStarPathfinder implements Pathfinder {

    // Base cost to move from one tile to another
    private final static float BASE_COST = 1.0f;
    private final static float INVALID_COST = 10.0f;
    
    private class PathNode {
        
        final HexCoordinate coord;
        final PathNode parent;
        // cost to get to this node from start of path
        final float cost;
        // estimated cost from here to target
        final float estimatedCost;
        final boolean invalid;

        public PathNode(HexCoordinate coord, PathNode parent) {
            this.coord = coord;
            this.parent = parent;
            Range range = map.getSector(coord).getRange();
            invalid  = (range == null || range.ordinal() > validRange.ordinal());
            // TODO: cost needs to include sector information from map
            this.cost = BASE_COST + (parent != null ? parent.cost : 0.0f) + (invalid ? INVALID_COST : 0.0f);
            this.estimatedCost = coord.getDistance(to);
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

    /**
     * Ordered list implementation.
     * 
     * @param <E> The element type.
     */
    private class PriorityList<E> extends LinkedList<E> {

        private final Comparator<E> comparator;
        
        public PriorityList(Comparator<E> comparator) {
            this.comparator = comparator;
        }
        
        @Override
        public boolean add(E e) {
            ListIterator<E> it = listIterator();
            while (it.hasNext()) {
                E el = it.next();
                if (comparator.compare(e, el) < 0) {
                    // at this point, we've gone too far - the new element's value
                    // is less than the current element's value, so move back
                    it.previous();
                    break;
                }
            }
            it.add(e);
            return true;
        }
    };
    
    private final Set<PathNode> visited;
    private final PriorityList<PathNode> candidates;
    private final Range validRange;
    private final FactionMap map;
    private HexCoordinate to;
    
    public AStarPathfinder(Range validRange, FactionMap map) {
        this.validRange = validRange;
        this.map = map;
        candidates = new PriorityList<>(new Comparator<PathNode>() {
            @Override
            public int compare(PathNode o1, PathNode o2) {
                return (int)(o1.estimatedCost - o2.estimatedCost);
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
                    res.push(new PathStep(cur.coord, cur.invalid));
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
            PathNode node = new PathNode(c, parent);
            if (visited.contains(node)) {
                continue; // skip nodes already on the path
            }
            // Check if we have a prior entry for these coordiantes
            int idx = candidates.indexOf(node);
            if (idx > -1) {
                PathNode prior = candidates.remove(idx);
                node = node.cost < prior.cost ? node : prior;
            }
            candidates.add(node);
        }
    }    
}