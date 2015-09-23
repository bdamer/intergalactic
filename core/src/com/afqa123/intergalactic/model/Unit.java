package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;

public interface Unit {
        
    String getId();

    String getType();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();

    double getBaseAttack();
    
    double getBaseDefense();
    
    /**
     * The normalized power value [0..1].
     * 
     * @return The power value.
     */
    double getPower();
    
    /**
     * Applies damage to this unit.
     * 
     * @param damage The amount of damage.
     */
    void applyDamage(double damage);
    
    /**
     * The absolute health value.
     * 
     * @return The health value.
     */
    double getHealth();
        
    void update(Session session);

    void refresh(Session session);
}