package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.UnitType.Action;
import com.afqa123.intergalactic.util.Path;

public interface Unit {
    
    String getId();

    void setId(String id);

    String getType();
    
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

    void refresh(Session state);
    
    /**
     * If possible will colonize the current sector. If successful, this action
     * will remove this unit from the session.
     * 
     * @param session The game session.
     * @return True if sector was colonized, otherwise false.
     */
    boolean colonizeSector(Session session);
    
    /**
     * If possible will build a station in the current sector. If successful,
     * this action will remove this unit from the session.
     * 
     * @param session The game session.
     * @return True if stations was build, otherwise false.
     */
    boolean buildStation(Session session);
}