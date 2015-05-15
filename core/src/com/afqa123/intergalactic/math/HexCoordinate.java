package com.afqa123.intergalactic.math;

import static com.afqa123.intergalactic.math.Hex.SQRT_THREE;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

public class HexCoordinate {

    public enum Direction {
        EAST(1, 0),
        NORTH_EAST(1, -1),
        NORTH_WEST(0, -1),
        WEST(-1, 0),
        SOUTH_WEST(-1, 1),
        SOUTH_EAST(0, 1);
        
        public final int x;
        public final int y;
        
        private Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    };
    
    public static final HexCoordinate ORIGIN = new HexCoordinate(0, 0);
    
    // Scale of the hex grid.
    private float scale = 1.0f;
    public final int x;
    public final int y;
    
    /**
     * Creates a new hex coordinate.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public HexCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a new hex coordinate.
     * 
     * @param v A vector.
     */
    public HexCoordinate(Vector2 v) {
        this.x = (int)v.x;
        this.y = (int)v.y;
    }
    
    /**
     * Creates a new hex coordinate from a position in world space (XZ).
     * 
     * @param world Position in world space.
     */
    public HexCoordinate(Vector3 world) {
        float xf = (world.x * SQRT_THREE / 3.0f - world.z / 3.0f) / scale;
        float yf = world.z * 2.0f / 3.0f / scale;
        Vector3 cube = new Vector3(xf, -xf - yf, yf); 
        Hex.round(cube);
        this.x = (int)cube.x;
        this.y = (int)cube.z;
    }
    
    /**
     * Returns this coordinate in world space (XZ).
     * 
     * @param coords The axial coordinates.
     * @return The world coordinates.
     */
    public Vector3 toWorld() {
        return new Vector3(
            scale * SQRT_THREE * ((float)x + (float)y / 2.0f),
            0.0f,
            scale * 3.0f / 2.0f * (float)y);
    }
    
    /**
     * Returns a cube representation of this coordinate.
     * 
     * @return A cube coordinate.
     */
    public Vector3 toCube() {
        return new Vector3(x, -x - y, y);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    } 

    /**
     * Returns the distance to another {@code HexCoordinate}.
     * 
     * @param c The other hex coordinate.
     * @return The distance.
     */
    public int getDistance(HexCoordinate c) {
        return (Math.abs(x - c.x) + Math.abs(x + y - c.x - c.y) + Math.abs(y - c.y)) / 2;
    }
    
    public HexCoordinate getNeighbor(Direction dir) {
        return new HexCoordinate(x + dir.x, y + dir.y);
    }

    public List<HexCoordinate> getRing(int radius) {
        List<HexCoordinate> res = new ArrayList<>();
        Direction[] directions = Direction.values();
        HexCoordinate cur = new HexCoordinate(x + directions[4].x * radius, 
            y + directions[4].y * radius);
        for (Direction dir : directions) {
            for (int j = 0; j < radius; j++) {
                res.add(cur);
                cur = cur.getNeighbor(dir);
            }
        }
        return res;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.x;
        hash = 53 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HexCoordinate other = (HexCoordinate) obj;
        return (x == other.x && y == other.y);
    }
    
    @Override
    public String toString() {
        return x + "," + y;
    }
}