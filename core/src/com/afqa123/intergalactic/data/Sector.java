package com.afqa123.intergalactic.data;

/**
 * Class representing a single galactic sector.
 */
public class Sector {

    public enum StarCategory {
        BLUE,       // gigantic
        WHITE,      // large
        YELLOW,     // average
        ORANGE,     // small
        RED         // dwarf
    }
    
    // Axial coordinates of this sector.
    private final int x;
    private final int y;
    private final StarCategory category;
    
    // Tile flags as relevant to player
    // TODO: do we have to determine this dynamically relative to each player?
    private boolean visible = true;
    private boolean shortRange;
    private boolean longRange;
    
    public Sector(int x, int y, StarCategory category) {
        this.x = x;
        this.y = y;
        this.category = category;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }    

    public StarCategory getCategory() {
        return category;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isShortRange() {
        return shortRange;
    }

    public void setShortRange(boolean shortRange) {
        this.shortRange = shortRange;
    }

    public boolean isLongRange() {
        return longRange;
    }

    public void setLongRange(boolean longRange) {
        this.longRange = longRange;
    }
}