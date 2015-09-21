package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.Session;

public class ExploreSectorPlan implements Plan {

    private enum Step {
        START,
        FIND_EXPLORER,
        FIND_IDLE_COLONY,
        BUILD_EXPLORER,
        MOVE_SHIP
    };
    
    private Goal goal;
    private Step step;
    private String shipId;
    private HexCoordinate productionSector;
    
    ExploreSectorPlan() {
        // required for serialization
    }
    
    public ExploreSectorPlan(Goal goal) {
        this.goal = goal;
        this.step = Step.START;
    }
    
    @Override
    public Status update(Session session, Faction faction) {
        Status res = null;
        Sector producer = null;
        Ship ship = null;
        //Gdx.app.debug("ExploreSectorPlan", "State = " + step.name());
        switch (step) {
            case START:
            case FIND_EXPLORER:
                boolean explorerExists = false;
                for (Ship s : faction.getShips()) {
                    // for now the only criteria for scouts is that they are long range
                    if (s.getRange() == Range.LONG) {
                        explorerExists = true;
                        // verify that ship is idle
                        if (s.getTarget() == null) {
                            ship = s;
                            break;
                        }
                    }
                }
                if (!explorerExists) {
                    // need to construct explorer ship
                    step = Step.FIND_IDLE_COLONY;
                    res = Status.ACTIVE;                    
                } else if (ship != null) {
                    shipId = ship.getId();
                    ship.selectTarget(session, goal.getTargetSector());
                    step = Step.MOVE_SHIP;
                    res = Status.ACTIVE;
                } else {
                    res = Status.INVALID;
                }
                break;
                
            case FIND_IDLE_COLONY:
                BuildTree tree = session.getBuildTree();
                // TODO: remove hardcoded reference
                BuildOption option = tree.getBuildOption("scout");
                for (Sector s : session.getGalaxy().getStarSystems()) {
                    if (s.isColony(faction) && s.isIdle() && tree.canBuild(s, option)) {
                        producer = s;
                        break;
                    }
                }
                if (producer != null) {
                    productionSector = producer.getCoordinates();
                    producer.getBuildQueue().add(new BuildQueueEntry(option.getId(), option.getLabel(), option.getCost()));
                    step = Step.BUILD_EXPLORER;
                    res = Status.ACTIVE;
                } else {
                    // I suppose this could also go into blocked and retry finding a producer next turn...
                    res = Status.INVALID;
                }             
                break;
                
            case BUILD_EXPLORER:
                producer = session.getGalaxy().getSector(productionSector);
                if (producer.getBuildQueue().isEmpty()) {
                    productionSector = null;
                    step = Step.FIND_EXPLORER;
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
                } else {
                    // check if we have reached the target
                    if (ship.getCoordinates().equals(goal.getTargetSector())) {
                        res = Status.COMPLETE;
                    // otherwise, move unit 
                    } else if (ship.move(session)) {
                        if (ship.getCoordinates().equals(goal.getTargetSector())) {
                            res = Status.COMPLETE;
                        } else {
                            res = Status.BLOCKED;
                        }
                    } else {
                        // unit was not able to move. attempt to compute new path
                        ship.selectTarget(session, goal.getTargetSector());
                        res = Status.ACTIVE;
                    }                    
                }
                break;
        }
        //Gdx.app.debug("ExploreSectorPlan", "New State = " + step.name());
        //Gdx.app.debug("ExploreSectorPlan", "Status = " + res.name());
        return res;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }
}
