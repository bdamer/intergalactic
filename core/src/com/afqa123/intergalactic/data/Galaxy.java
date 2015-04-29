package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.data.Sector.StarCategory;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Galaxy {

    // Size of the galaxy represented by the radius of the surrounding circle.
    private final int size;
    // Number of sectors in the galaxy
    private int count;
    // Sectors in this galaxy. The first index represents the row, the second
    // the column.
    private final Sector[][] sectors;
    // List of systems which contain stars.
    private final List<Sector> starSystems;
    
    /**
     * Creates a new galaxy of a given size.
     * 
     * @param size The radius of the circle that surrounds the hexagon galaxy.
     */
    public Galaxy(int size) {
        this.size = size;
        this.starSystems = new ArrayList<>();
        // build up array of variable size for each row
        int rows = size * 2 - 1;
        int cols = size * 2 - 1;
        sectors = new Sector[rows][];
        int median = size - 1;
        sectors[median] = new Sector[cols];
        for (int i = 1; i < size; i++) {
            cols--;
            sectors[median - i] = new Sector[cols];
            sectors[median + i] = new Sector[cols];
        }
        initializeSectors();
    }
    
    private void initializeSectors() {
        count = 0;
        for (int y = 0; y < sectors.length; y++) {
            for (int x = 0; x < sectors[y].length; x++) {
                Vector2 axial = offsetToAxial(x, y);
                
                // TODO: revisit and come up with proper map generation mechanism
                boolean hasStar = (Math.random() < 0.1);
                StarCategory category = null;
                if (hasStar) {
                    switch ((int)(Math.random() * 5)) {
                        case 0:
                            category = StarCategory.BLUE;
                            break;
                        case 1:
                            category = StarCategory.ORANGE;
                            break;
                        case 2:
                            category = StarCategory.RED;
                            break;
                        case 3:
                            category = StarCategory.WHITE;
                            break;
                        case 4:
                            category = StarCategory.YELLOW;
                            break;
                    }
                }
                Sector s = new Sector(new HexCoordinate(axial), category);
                sectors[y][x] = s;                
                if (hasStar) {
                    starSystems.add(s);
                }
                count++;
            }
        }
    }
    
    /**
     * Returns the number of sectors in the galaxy.
     * 
     * @return The count.
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Returns all sectors in the galaxy.
     * 
     * @return The sectors.
     */
    public Sector[][] getSectors() {
        return sectors;
    }
    
    /**
     * Returns the sector at a given set of axial coordinates.
     * 
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return A sector or null.
     */
    public Sector getSector(int x, int y) {
        int row = y + size - 1;
        int col = (y < 0 ? x + row : x + size - 1);
        return sectors[row][col];
    }

    public List<Sector> getStarSystems() {
        return starSystems;
    }    
    
    /**
     * Converts a set of axial coordinates to offset coordinates for this grid.
     * 
     * @param axial Axial coordinates.
     * @return Offset coordinates.
     */
    public Vector2 axialToOffset(Vector2 axial) {
        return axialToOffset((int)axial.x, (int)axial.y);
    }

    /**
     * Converts a set of axial coordinates to offset coordinates for this grid.
     * 
     * @param xa Axial X coordinate.
     * @param ya Axial Y coordinate.
     * @return Offset coordinates.
     */
    public Vector2 axialToOffset(int xa, int ya) {
        int row = ya + size - 1;
        int col = (ya < 0 ? xa + row : xa + size - 1);
        return new Vector2(col, row);
    }    
    
    /**
     * Converts a set of offset coordinates to axial coordinates for this grid.
     * 
     * @param offset Offset coordinates.
     * @return Axial coordinates.
     */
    public Vector2 offsetToAxial(Vector2 offset) {
        return offsetToAxial((int)offset.x, (int)offset.y);
    }

    /**
     * Converts a set of offset coordinates to axial coordinates for this grid.
     * 
     * @param xo Offset X coordinate.
     * @param yo Offset Y coordinate.
     * @return Axial coordinates.
     */
    public Vector2 offsetToAxial(int xo, int yo) {
        int y = yo - size + 1;
        int x = (y < 0 ? xo - yo : xo - size + 1);
        return new Vector2(x, y);
    }
}