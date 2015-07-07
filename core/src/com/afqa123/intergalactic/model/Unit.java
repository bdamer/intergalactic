package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;

public interface Unit {
        
    String getId();

    String getType();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();

    void update(Session session);

    void refresh(Session session);
}