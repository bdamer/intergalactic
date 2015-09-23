package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.ShipType.Action;
import com.afqa123.intergalactic.model.Unit;

public class BuildStationPlan implements Plan {

    private enum Step {
        START,
        FIND_STATION_SHIP,
        FIND_IDLE_COLONY,
        BUILD_STATION_SHIP,
        MOVE_SHIP
    };
    
    private Goal goal;
    private Step step;
    private HexCoordinate productionSector;
    private String shipId;
    
    BuildStationPlan() {
        // required for serialization
    }
    
    public BuildStationPlan(Goal goal) {
        this.goal = goal;
        this.step = Step.START;
    }
    
    private boolean isTargetValid(Session session) {
        Sector colony = session.getGalaxy().getSector(goal.getTargetSector());
        if (!colony.canBuildOutpost()) {
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
        
        // Always check if target is still valid.
        if (!isTargetValid(session)) {
            return Status.INVALID;
        }
        
        switch (step) {
            case START:
            case FIND_STATION_SHIP:
                for (Ship s : faction.getShips()) {
                    // verify that ship is idle and can create new station
                    if (s.getTarget() == null && s.canPerformAction(Action.BUILD_STATION)) {
                        ship = s;
                        break;
                    }
                }
                if (ship != null) {
                    // use found ship
                    shipId = ship.getId();
                    ship.selectTarget(session, goal.getTargetSector());
                    step = Step.MOVE_SHIP;
                    res = Status.ACTIVE;
                } else {
                    // need to construct ship
                    step = Step.FIND_IDLE_COLONY;
                    res = Status.ACTIVE;
                }
                break;
                
            case FIND_IDLE_COLONY:
                BuildTree tree = session.getBuildTree();
                // TODO: remove hardcoded reference - can we search for any build options that can perform Action.BUILD_STATION?
                BuildOption option = tree.getBuildOption("outpost");
                for (Sector s : session.getGalaxy().getStarSystems()) {
                    if (s.isColony(faction) && s.isIdle() && tree.canBuild(s, option)) {
                        producer = s;
                        break;
                    }
                }
                if (producer != null) {
                    productionSector = producer.getCoordinates();
                    producer.getBuildQueue().add(new BuildQueueEntry(option.getId(), option.getLabel(), option.getCost()));
                    step = Step.BUILD_STATION_SHIP;
                    res = Status.ACTIVE;
                } else {
                    // I suppose this could also go into blocked and retry finding a producer next turn...
                    res = Status.INVALID;
                }             
                break;
                
            case BUILD_STATION_SHIP:
                producer = session.getGalaxy().getSector(productionSector);
                if (producer.getBuildQueue().isEmpty()) {
                    productionSector = null;
                    step = Step.FIND_STATION_SHIP;
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
                    if (ship.buildStation(session)) {
                        res = Status.COMPLETE;
                    } else {
                        res = Status.INVALID;
                    }
                } else {
                    // if at any point the goal sector becomes occupied, mark plan as invalid
                    Sector targetSector = session.getGalaxy().getSector(goal.getTargetSector());
                    if (!targetSector.canBuildOutpost()) {
                        res = Status.INVALID;
                    // unit still moving to target
                    } else if (ship.move(session)) {
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