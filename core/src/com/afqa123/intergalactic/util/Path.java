package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.Path.PathStep;
import java.io.Serializable;
import java.util.LinkedList;

public class Path extends LinkedList<PathStep> {
    
    public static class PathStep implements Serializable {
        
        private static final long serialVersionUID = 1l;
        public final HexCoordinate coordinate;
        public final float cost;
        public final boolean attack;
        
        public PathStep(HexCoordinate coordinate, float cost, boolean attack) {
            this.coordinate = coordinate;
            this.cost = cost;
            this.attack = attack;
        }        
    };
}