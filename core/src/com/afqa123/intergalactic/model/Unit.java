package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.UnitType.Action;
import com.afqa123.intergalactic.util.Path;

// TODO: revisit this - the unit interface contains too many methods only 
// available in some of the subclasses
public interface Unit {
    
    public enum MovementResult {
        MOVED,
        AT_GOAL,
        BLOCKED
    }
    
    String getId();

    void setId(String id);

    String getType();
    
    Faction getOwner();
    
    HexCoordinate getCoordinates();

    int getScanRange();
    
    float getMovementPoints();
    
    Path getPath();
    
    HexCoordinate getTarget();
    
    void selectTarget(Session session, HexCoordinate target);
    
    /**
     * Moves this unit towards its target.
     * 
     * @param session The game session.
     * @return True if the unit was moved, false if it could not move or was 
     * blocked.
     */
    boolean move(Session session);
    
    boolean isReadyForStep();
    
    void step();

    /**
     * Checks if this unit can attack another unit.
     * 
     * @param unit The unit to attack.
     * @return True if unit can attack, otherwise false.
     */
    boolean canAttack(Unit unit);
    
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
    
    void wake();
}