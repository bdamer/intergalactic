package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.Unit;

public class DestroyUnitPlan implements Plan {

    private enum Step {
        START,
        FIND_SHIP,
        MOVE_SHIP,
        ATTACK
    };
    
    private Goal goal;
    private Step step;
    private String shipId;
    
    DestroyUnitPlan() {
        // required for serialization
    }
    
    public DestroyUnitPlan(Goal goal) {
        this.goal = goal;
        this.step = Step.START;
    }
    
    @Override
    public Status update(Session session, Faction faction) {
        Status res = null;
        Ship ship = null;
        Unit target = session.findUnit(goal.getTargetUnitId());
        
        switch (step) {
            case START:
            case FIND_SHIP:
                // Check that target ship still exists
                if (target != null) {
                    HexCoordinate targetPos = target.getCoordinates();
                    for (Ship s : faction.getShips()) {
                        // verify that ship is idle, can attack, and is within visible range of target
                        if (s.getTarget() == null && s.canPerformAction(ShipType.Action.ATTACK) &&
                                targetPos.getDistance(s.getCoordinates()) <= s.getScanRange()) {
                            ship = s;
                            break;
                        }
                    }
                    if (ship != null) {
                        shipId = ship.getId();
                        ship.selectTarget(session, targetPos);
                        step = Step.MOVE_SHIP;
                        res = Status.ACTIVE;
                    } else {
                        res = Status.BLOCKED;
                    }
                } else {
                    res = Status.COMPLETE;
                }
                break;
                
            case MOVE_SHIP:
                ship = (Ship)session.findUnit(shipId);
                if (target != null) {
                    HexCoordinate targetPos = target.getCoordinates();
                    
                    if (ship == null) {
                        // unit destroyed?
                        res = Status.INVALID;
                    // target has moved - reevaluate path
                    } else if (!targetPos.equals(ship.getTarget())) {
                        ship.selectTarget(session, targetPos);
                        res = Status.ACTIVE;
                    } else {
                        // TODO: fixme - need to handle what to do after attack
                        
                        // unit still moving to target
                        if (ship.move(session)) {
                            res = Status.BLOCKED;
                        } else {
                            // unit was not able to move. attempt to compute new path
                            ship.selectTarget(session, targetPos);
                            res = Status.ACTIVE;
                        }
                    }
                } else {
                    // reset ship target so it can be used by other plans
                    if (ship != null) {
                        ship.selectTarget(session, null);
                    }
                    res = Status.COMPLETE;
                }
                break;
        }
        return res;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }    
}