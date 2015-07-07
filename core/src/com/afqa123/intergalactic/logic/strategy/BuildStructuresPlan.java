package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.logic.BuildPlan;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.StructureType;
import com.badlogic.gdx.utils.Json;

public class BuildStructuresPlan implements Plan {

    private enum Step {
        START,
        PICK_STRUCTURE,
        BUILD        
    };
    
    private Goal goal;
    private BuildPlan plan;
    private Step step;
    
    BuildStructuresPlan() {
        // required for serialization
        Json json = new Json();
        String raw = Assets.get("data/plans/plan01.json");
        this.plan = json.fromJson(BuildPlan.class, raw);
    }
    
    public BuildStructuresPlan(Goal goal) {
        this.goal = goal;
        Json json = new Json();
        String raw = Assets.get("data/plans/plan01.json");
        this.plan = json.fromJson(BuildPlan.class, raw);
        this.step = Step.START;
    }
    
    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public Status update(Session session, Faction faction) {
        Status res = null;
        Sector sector = session.getGalaxy().getSector(goal.getTargetSector());
        switch (step) {
            case START:
                // check that sector is in fact idle
                if (!sector.getBuildQueue().isEmpty()) {
                    res = Status.INVALID;
                } else {
                    res = Status.ACTIVE;
                    step = Step.PICK_STRUCTURE;
                }
                break;                
            case PICK_STRUCTURE:
                String id = plan.getNextStructure(sector);
                if (id == null) {
                    res = Status.INVALID;
                } else {
                    // add id to build queue
                    StructureType struct = session.getDatabase().getStructure(id);
                    sector.getBuildQueue().add(new BuildQueueEntry(id, struct.getLabel(), struct.getCost()));
                    res = Status.ACTIVE;
                    step = Step.BUILD;
                }
                break;
            case BUILD:
                // done building, so switch back to picking a new structure
                if (sector.getBuildQueue().isEmpty()) {
                    step = Step.PICK_STRUCTURE;
                    res = Status.ACTIVE;
                } else {
                    res = Status.BLOCKED;
                }
                break;
        }
        return res;
    }
}