package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.util.Path;

public interface Unit {

    enum Action {
        BUILD_OUTPOST, COLONIZE, INTERCEPT
    };
    
    String getId();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();
    
    float getMovementPoints();
    
    Path getPath();
    
    HexCoordinate getTarget();
    
    void selectTarget(HexCoordinate target);
    
    void move();
    
    boolean isReadyForStep();
    
    void step();
    
    boolean canPerformAction(Action action);

    void refresh(State state);
}