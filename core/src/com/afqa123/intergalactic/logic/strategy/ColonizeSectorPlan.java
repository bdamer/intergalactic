package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.logic.strategy.Plan.Status;
import com.afqa123.intergalactic.logic.strategy.SimpleStrategy.FactionState;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.UnitType.Action;

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
    
    @Override
    public Status update(Session state, FactionState fs) {
        Status res = null;
        Sector producer = null;
        Ship colonizer = null;
        switch (step) {
            case START:
            case FIND_COLONY_SHIP:
                for (Ship s : fs.idleShips) {
                    // verify that ship is idle and can create new colony
                    if (s.getTarget() == null && s.canPerformAction(Action.COLONIZE)) {
                        colonizer = s;
                        break;
                    }
                }
                if (colonizer != null) {
                    // use found colony ship
                    shipId = colonizer.getId();
                    colonizer.selectTarget(goal.getTargetSector());
                    step = Step.MOVE_SHIP;
                    res = Status.ACTIVE;
                } else {
                    // need to construct colony ship
                    step = Step.FIND_IDLE_COLONY;
                    res = Status.ACTIVE;
                }
                break;
                
            case FIND_IDLE_COLONY:
                BuildTree tree = state.getBuildTree();
                // TODO: remove hardcoded reference - can we search for any build options that can perform Action.COLONIZE?
                BuildOption option = tree.getBuildOption("colony_ship");
                for (Sector s : fs.idleSectors) {
                    if (s.getBuildQueue().isEmpty() && tree.canBuild(s, option)) {
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
                producer = fs.galaxy.getSector(productionSector);
                if (producer.getBuildQueue().isEmpty()) {
                    productionSector = null;
                    step = Step.FIND_COLONY_SHIP;
                    res = Status.ACTIVE;
                } else {
                    res = Status.BLOCKED;
                }
                break;

            case MOVE_SHIP:
                colonizer = (Ship)state.findUnit(shipId);
                if (colonizer == null) {
                    // unit destroyed?
                    res = Status.INVALID; // again, we could move back into the initial state 
                                          // but it's probably cleaner to re-evaluate the goal
                } else if (colonizer.getCoordinates().equals(goal.getTargetSector())) {
                    // attempt to colonize
                    if (colonizer.colonizeSector(state)) {
                        res = Status.COMPLETE;
                    } else {
                        res = Status.INVALID;
                    }
                } else {
                    // unit still moving to target
                    colonizer.move();
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