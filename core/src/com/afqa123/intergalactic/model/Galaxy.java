package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Galaxy data structure containing all sectors for a game.
 */
public final class Galaxy implements Json.Serializable {

    // Size of the galaxy represented by the radius of the surrounding circle.
    private int radius;
    // Number of sectors in the galaxy
    private int count;
    // Sectors in this galaxy. The first index represents the row, the second
    // the column.
    private Sector[][] sectors;
    // List of systems which contain stars.
    private final List<Sector> starSystems = new ArrayList<>();
    
    Galaxy() {
        // required for serialization
    }
    
    /**
     * Creates a new galaxy of a given size.
     * 
     * @param radius The radius of the circle that surrounds the hexagon galaxy.
     */
    public Galaxy(int radius) {
        resize(radius);
    }
    
    private void resize(int radius) {
        this.radius = radius;
        // build up array of variable size for each row
        int rows = radius * 2 - 1;
        int median = radius - 1;
        int cols = radius;
        sectors = new Sector[rows][];
        for (int i = 0; i < rows; i++) {
            sectors[i] = new Sector[cols];
            for (int j = 0; j < cols; j++) {
                sectors[i][j] = new Sector(null, new HexCoordinate(offsetToAxial(j, i)), null);
                count++;
            }
            if (i < median) {
                cols++;
            } else if (i >= median) {
                cols--;
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
    
    public void addStarSystem(Sector s) {
        HexCoordinate coord = s.getCoordinates();
        Vector2 offset = axialToOffset(coord.x, coord.y);
        starSystems.add(s);
        sectors[(int)offset.y][(int)offset.x] = s;
    }
    
    public List<Sector> getStarSystems() {
        return starSystems;
    }    

    public List<Sector> getFactionSystems(Faction faction) {
        List<Sector> res = new ArrayList<>();
        for (Sector s : starSystems) {
            String owner = s.getOwner();
            if (owner != null && owner.equals(faction.getName())) {
                res.add(s);
            }
        }
        return res;
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

    @Override
    public void write(Json json) {
        json.writeValue("radius", radius);
        json.writeValue("systems", starSystems);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        resize(json.readValue("radius", Integer.class, jv));
        Sector[] stars = json.readValue("systems", Sector[].class, jv);
        for (Sector star : stars) {
            addStarSystem(star);
        }
    }
}