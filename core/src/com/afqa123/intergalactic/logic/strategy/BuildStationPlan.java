package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.UnitType;

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
    
    @Override
    public Status update(Session session, SimpleStrategy.FactionState fs) {
        Status res = null;
        Sector producer = null;
        Ship ship = null;
        switch (step) {
            case START:
            case FIND_STATION_SHIP:
                for (Ship s : fs.idleShips) {
                    // verify that ship is idle and can create new station
                    if (s.getTarget() == null && s.canPerformAction(UnitType.Action.BUILD_STATION)) {
                        ship = s;
                        break;
                    }
                }
                if (ship != null) {
                    // use found ship
                    shipId = ship.getId();
                    ship.selectTarget(goal.getTargetSector());
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
                for (Sector s : fs.idleSectors) {
                    if (tree.canBuild(s, option)) {
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
                producer = fs.galaxy.getSector(productionSector);
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
                    // unit still moving to target
                    ship.move(session);
                    res = Status.BLOCKED;
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