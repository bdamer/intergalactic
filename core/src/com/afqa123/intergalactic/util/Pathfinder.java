package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.math.HexCoordinate;

public interface Pathfinder {

    /**
     * Finds a path from one coordinate to another.
     * 
     * @param from The start of the path.
     * @param to The goal of the path.
     * @return A {@code Path}, or null if no path was found.
     */
    Path findPath(HexCoordinate from, HexCoordinate to);
    
}