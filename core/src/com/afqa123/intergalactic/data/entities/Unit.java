package com.afqa123.intergalactic.data.entities;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.Path;

public interface Unit {

    String getId();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();
    
    Path getPath();
    
    void selectTarget(HexCoordinate target);
    
    void move();
    
    boolean isReadyForStep();
    
    void step();
}