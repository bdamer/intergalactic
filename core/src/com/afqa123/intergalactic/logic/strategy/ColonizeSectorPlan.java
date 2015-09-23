package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.logic.strategy.Plan.Status;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.ShipType.Action;
import com.afqa123.intergalactic.model.Unit;

public class ColonizeSectorPlan implements Plan {

    private enum Step {
        START,
        FIND_COLONY_SHIP,
        FIND_IDLE_COLONY,
        BUILD_COLONY_SHIP,
        MOVE_SHIP
    };
    
    private Goal goal;
    private Step step;
    private HexCoordinate productionSector;
    private String shipId;
    
    ColonizeSectorPlan() {
        // required for serialization
    }
    
    public ColonizeSectorPlan(Goal goal) {
        this.goal = goal;
        this.step = Step.START;
    }
    
    private boolean isTargetValid(Session session) {
        Sector colony = session.getGalaxy().getSector(goal.getTargetSector());
        if (!colony.canColonize()) {
            return false;
        }
        Unit u = session.findUnitInSector(goal.getTargetSector());
        if (u != null && !u.getId().equals(shipId)) {
            return false;
        }
        return true;
    }
    
    @Override
    public Status update(Session session, Faction faction) {
        Status res = null;
        Sector producer = null;
        Ship ship = null;

        // always check if target is still valid
        if (!isTargetValid(session)) {
            return Status.INVALID;
        }
        
        switch (step) {
            case START:
            case FIND_COLONY_SHIP:
                for (Ship s : faction.getShips()) {
                    // verify that ship is idle and can create new colony
                    if (s.getTarget() == null && s.canPerformAction(Action.COLONIZE)) {
                        ship = s;
                        break;
                    }
                }
                if (ship != null) {
                    // use found colony ship
                    shipId = ship.getId();
                    ship.selectTarget(session, goal.getTargetSector());
                    step = Step.MOVE_SHIP;
                    res = Status.ACTIVE;
                } else {
                    // need to construct colony ship
                    step = Step.FIND_IDLE_COLONY;
                    res = Status.ACTIVE;
                }
                break;
                
            case FIND_IDLE_COLONY:
                BuildTree tree = session.getBuildTree();
                // TODO: remove hardcoded reference - can we search for any build options that can perform Action.COLONIZE?
                BuildOption option = tree.getBuildOption("colony_ship");
                for (Sector s : session.getGalaxy().getStarSystems()) {
                    if (s.isColony(faction) && s.isIdle() && tree.canBuild(s, option)) {
                        producer = s;
                        break;
                    }
                }
                if (producer != null) {
                    productionSector = producer.getCoordinates();
                    producer.getBuildQueue().add(new BuildQueueEntry(option.getId(), option.getLabel(), option.getCost()));
                    step = Step.BUILD_COLONY_SHIP;
                    res = Status.ACTIVE;
                } else {
                    // I suppose this could also go into blocked and retry finding a producer next turn...
                    res = Status.INVALID;
                }             
                break;
                
            case BUILD_COLONY_SHIP:
                producer = session.getGalaxy().getSector(productionSector);
                if (producer.getBuildQueue().isEmpty()) {
                    productionSector = null;
                    step = Step.FIND_COLONY_SHIP;
                    res = Status.ACTIVE;
                } else {
                    res = Status.BLOCKED;
                }
                break;

            case MOVE_SHIP:
                ship = (Ship)session.findUnit(shipId);
                if (ship == null) {
                    // unit destroyed?
                    res = Status.INVALID; // again, we could move back into the initial state 
                                          // but it's probably cleaner to re-evaluate the goal
                } else if (ship.getCoordinates().equals(goal.getTargetSector())) {
                    // attempt to colonize
                    if (ship.colonizeSector(session)) {
                        res = Status.COMPLETE;
                    } else {
                        res = Status.INVALID;
                    }
                } else {
                    // unit still moving to target
                    if (ship.move(session)) {
                        res = Status.BLOCKED;
                    } else {
                        // unit was not able to move. attempt to compute new path
                        ship.selectTarget(session, goal.getTargetSector());
                        res = Status.ACTIVE;
                    }
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