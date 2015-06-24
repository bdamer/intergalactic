package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.data.model.Structure;
import com.afqa123.intergalactic.data.entities.Sector;
import com.afqa123.intergalactic.data.model.BuildOption;

public class BuildQueueEntry {

    private final Sector sector;
    private final BuildOption option;
    private float cost;
    private int turns;

    /**
     * Creates a new {@code BuildQueueEntry}.
     * 
     * @param sector The sector containing the build queue.
     * @param option The build option.
     */
    public BuildQueueEntry(final Sector sector, final BuildOption option) {
        this.sector = sector;
        this.option = option;
        this.cost = option.getCost();
        computeTurns();
    }
    
    public BuildOption getBuildOption() {
        return option;
    }
    
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void computeTurns() {
        turns = -1;
        if (sector.getIndustrialOutput() > 0.0f) {
            turns = Math.round(cost / sector.getIndustrialOutput());
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d)", option.getLabel(), turns);
    }
}