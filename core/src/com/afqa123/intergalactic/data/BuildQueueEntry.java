package com.afqa123.intergalactic.data;

public class BuildQueueEntry {

    private final Sector sector;
    private final Structure structure;
    private float cost;
    private int turns;

    /**
     * Creates a new {@code BuildQueueEntry}.
     * 
     * @param secctor The sector containing the build queue.
     * @param id The structure id.
     * @param label The structure label.
     * @param initialCost The initial cost of the structure.
     */
    public BuildQueueEntry(final Sector sector, final Structure structure) {
        this.sector = sector;
        this.structure = structure;
        this.cost = structure.getCost();
        computeTurns();
    }
    
    public Structure getStructure() {
        return structure;
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
        return String.format("%s (%d)", structure.getLabel(), turns);
    }
}