package com.afqa123.intergalactic.data;

/**
 * Class representing a single galactic sector.
 */
public class Sector {

    // Axial coordinates of this sector.
    private final int x;
    private final int y;
    // Tile flags as relevant to player
    // TODO: do we have to determine this dynamically relative to each player?
    private boolean visible = true;
    private boolean shortRange;
    private boolean longRange;
    
    public Sector(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
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