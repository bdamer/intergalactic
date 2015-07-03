package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.UnitType.Action;
import com.afqa123.intergalactic.util.Path;

public interface Unit {
    
    String getId();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();
    
    float getMovementPoints();
    
    Path getPath();
    
    HexCoordinate getTarget();
    
    void selectTarget(HexCoordinate target);
    
    // TODO: this seems specific to ships - revisit and see if we can clean up
    void move();
    
    boolean isReadyForStep();
    
    void step();
    
    boolean canPerformAction(Action action);

    void refresh(State state);
}