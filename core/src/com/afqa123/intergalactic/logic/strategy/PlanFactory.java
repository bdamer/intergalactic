package com.afqa123.intergalactic.logic.strategy;

public class PlanFactory {

    private PlanFactory() {
        
    }
    
    public static Plan newPlan(Goal goal) {
        switch (goal.getType()) {
            case BUILD_STRUCTURES:
                return new BuildStructuresPlan(goal);
            case COLONIZE_SECTOR:
                return new ColonizeSectorPlan(goal);
            case EXPLORE:
                return new ExploreSectorPlan(goal);
            case BUILD_STATION:
                return new BuildStationPlan(goal);
            case DESTROY_UNIT:
            default:
                return null;
        }
    }    
}