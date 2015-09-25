package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;

public interface Unit {
        
    String getId();

    String getType();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();

    float getBaseAttack();
    
    float getBaseDefense();
    
    /**
     * The normalized power value [0..1].
     * 
     * @return The power value.
     */
    float getPower();
    
    /**
     * Applies damage to this unit.
     * 
     * @param damage The amount of damage.
     */
    void applyDamage(float damage);

    /**
     * Returns the absolute health of this unit.
     * 
     * @return The health value.
     */
    float getHealth();
            
    void update(Session session);

    void refresh(Session session);
}