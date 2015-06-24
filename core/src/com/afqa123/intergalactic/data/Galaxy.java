package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.data.entities.Sector;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.entities.Sector.StarCategory;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class Galaxy {

    // Prime number use to generate randomized sector name order
    private static final int RANDOM_STEP = 19;
    // Size of the galaxy represented by the radius of the surrounding circle.
    private final int radius;
    // Number of sectors in the galaxy
    private int count;
    // Sectors in this galaxy. The first index represents the row, the second
    // the column.
    private final Sector[][] sectors;
    // List of systems which contain stars.
    private final List<Sector> starSystems;
    private final String[] sectorNames;
    private int lastNameIdx;
    
    /**
     * Creates a new galaxy of a given size.
     * 
     * @param radius The radius of the circle that surrounds the hexagon galaxy.
     */
    public Galaxy(int radius) {
        this.radius = radius;
        this.starSystems = new ArrayList<>();        
        sectorNames = ((JsonValue)Assets.get("data/sectors.json")).asStringArray();
        lastNameIdx = (int)(Math.random() * sectorNames.length);
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        sectors = new Sector[rows][];
        for (int i = 0; i < rows; i++) {
            sectors[i] = new Sector[cols];
            if (i < median) {
                cols++;
            } else if (i > median) {
                cols--;
            }
        }
        initializeSectors();
    }
    
    private void initializeSectors() {
        starSystems.clear();
        count = 0;
        for (int y = 0; y < sectors.length; y++) {
            for (int x = 0; x < sectors[y].length; x++) {
                sectors[y][x] = new Sector(null, new HexCoordinate(offsetToAxial(x, y)), null);
                count++;
            }
        }
    }
    
    public void randomizeSectors() {
        Gdx.app.log(Galaxy.class.getName(), "Building irregular galaxy.");
        for (int y = 0; y < sectors.length; y++) {
            for (int x = 0; x < sectors[y].length; x++) {
                // TODO: revisit and come up with proper map generation mechanism
                boolean hasStar = (Math.random() < 0.1);
                if (hasStar) {
                    Vector2 axial = offsetToAxial(x, y);
                    sectors[y][x] = createStarSector(new HexCoordinate(axial));
                }            
            }
        }
    }
    
    public void randomizeSectorsRing() {
        Gdx.app.log(Galaxy.class.getName(), "Building ring galaxy.");
        // TODO: implement
    }
    
    public void randomizeSectorsSpiral() {
        Gdx.app.log(Galaxy.class.getName(), "Building spiral galaxy.");
        
        final float MIN_ANGLE = 0.4f;  // min increase in angle per step
        final float MAX_ANGLE = 1.5f;  // max increase in angle per step        
        // TODO: HEIGHT * width at mid point should give us max radius
        final float RADIUS = 20.0f;     // radius of the spiral 
        // TODO: base on size of galaxy [tiny, small, medium, large, huge]
        final float NUM_ROT = 3.0f;     // number of circles in this spiral
        
        float ratio = 0.0f;
        float angle = 0.0f;
        while (ratio < 1.0) {
            // determine the ratio of the current angle to the max angle 
            ratio = angle / (NUM_ROT * (float)(2.0 * Math.PI));
            Vector3 pos = new Vector3((float)Math.cos(angle) * RADIUS * ratio,
                                      0.0f,
                                      (float)Math.sin(angle) * RADIUS * ratio);
            HexCoordinate coord = new HexCoordinate(pos);
            Vector2 offset = axialToOffset((int)coord.x, (int)coord.y);
            
            // only create star sector if there currently is none at these coordinates.
            // TODO: additionally, perform check that there are no stars next to this
            if (sectors[(int)offset.y][(int)offset.x].getCategory() == null) {
                sectors[(int)offset.y][(int)offset.x] = createStarSector(coord);
            }
            
            // Angle changes based on inverse ratio, so that we end up with an
            // event distribution (i.e., the increase in angle gets smaller
            // as the radius increases).
            angle += MIN_ANGLE + Math.random() * (1.0f - ratio) * (MAX_ANGLE - MIN_ANGLE);
        }
    }
    
    private Sector createStarSector(HexCoordinate coord) {
        StarCategory category = null;
        String name = sectorNames[(lastNameIdx += RANDOM_STEP) % sectorNames.length];
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
        Sector s = new Sector(name, coord, category);
        starSystems.add(s);
        return s;
    }
    
    /**
     * Returns the number of sectors in the galaxy.
     * 
     * @return The count.
     */
    public int getCount() {
        return count;
    }
    
    public int getRadius() {
        return radius;
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
    public Sector getSector(HexCoordinate c) {
        int row = c.y + radius - 1;
        int col = (c.y < 0 ? c.x + row : c.x + radius - 1);
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
        int row = ya + radius - 1;
        int col = (ya < 0 ? xa + row : xa + radius - 1);
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
        int y = yo - radius + 1;
        int x = (y < 0 ? xo - yo : xo - radius + 1);
        return new Vector2(x, y);
    }
}