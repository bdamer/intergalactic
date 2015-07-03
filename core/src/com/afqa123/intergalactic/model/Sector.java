package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.math.Vector3;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Class representing a single galactic sector.
 */
public final class Sector {

    // TODO: review
    private static final int BASE_FOOD_PRODUCTION = 0;
    private static final int BASE_IND_PRODUCTION = 0;
    private static final int BASE_SCI_PRODUCTION = 0;    
    private static final float TURBULENCE = 1.0f / 50000.0f;
    private String name;
    private String owner;
    
    // Axial coordinates of this sector.
    private HexCoordinate coordinates;
    private StarType type;
    
    // Game stats (updated each turn)
    private double population;
    private int maxPopulation;
    private double growthRate;

    private float morale;
    private float foodConsumptionRate;
    // Members of population allocated to production
    private int foodProducers;
    private int industrialProducers;
    private int scienceProducers;
    // Production multipliers
    private float foodMultiplier;
    private float industrialMultiplier;
    private float scienceMultiplier;
    // Buildings and production queue
    private Set<String> structures;
    private Queue<BuildQueueEntry> buildQueue;
    
    // Rendering properties
    // Base color / material
    // TODO: review - currently not used
    private Vector3 material;
    // Scale of system on map
    private float scale;
    // Color gradient
    private float gradient;
    // Surface turbulence
    private float turbulence;
    // seed value to animate surface
    private long seed;
    
    Sector() {
        // required for serialization
    }
    
    public Sector(String name, HexCoordinate coordinates, StarType type) {
        this.name = name;
        this.coordinates = coordinates;
        this.type = type;
        this.seed = System.currentTimeMillis() - (long)(Math.random() * 1000000.0);

        this.population = 0.0;
        // TODO: this is based on the number of terraformed planets in this sector
        this.maxPopulation = 0;
        this.foodProducers = 0;
        this.structures = new HashSet<>();
        this.buildQueue = new LinkedList<>();
        updateModifiers();
        
        if (type != null) {
            switch (type) {
                case BLUE:
                    scale = 1.0f;
                    turbulence = TURBULENCE;
                    material = new Vector3(0.0f, 0.0f, 1.0f);
                    gradient = 20.0f / 32.0f;
                    break;
                case WHITE:
                    scale = 0.8f;
                    turbulence = TURBULENCE;
                    material = new Vector3(1.0f, 1.0f, 1.0f);
                    gradient = 15.0f / 32.0f;
                    break;
                case YELLOW:
                    scale = 0.6f;
                    turbulence = TURBULENCE;
                    material = new Vector3(1.0f, 1.0f, 0.0f);
                    gradient = 10.0f / 32.0f;
                    break;
                case ORANGE:
                    scale = 0.5f;
                    turbulence = TURBULENCE;
                    material = new Vector3(1.0f, 0.35f, 0.0f);
                    gradient = 5.0f / 32.0f;
                    break;
                case RED:
                    scale = 0.3f;
                    turbulence = TURBULENCE;
                    material = new Vector3(1.0f, 0.0f, 0.0f);
                    gradient = 0.0f / 32.0f;
                    break;
                default:
                    throw new RuntimeException("Unsupported sector category: " + type);
            }                    
        } else {
            material = null;
            scale = 0.0f;
            gradient = 0.0f;
            turbulence = 0.0f;
        }
    }
    
    public HexCoordinate getCoordinates() {
        return coordinates;
    }

    public StarType getType() {
        return type;
    }

    public Vector3 getMaterial() {
        return material;
    }

    public float getScale() {
        return scale;
    }

    public float getGradient() {
        return gradient;
    }

    public float getTurbulence() {
        return turbulence;
    }

    public long getSeed() {
        return seed;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }    
    
    public double getPopulation() {
        return population;
    }

    public void setPopulation(double population) {
        this.population = population;
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public void setMaxPopulation(int maxPopulation) {
        this.maxPopulation = maxPopulation;
    }    
    
    public Morale getMorale() {
        if (morale < 0.2f) {
            return Morale.REBELLIOUS;
        } else if (morale < 0.4f) {
            return Morale.DISGRUNTLED;
        } else if (morale < 0.6f) {
            return Morale.CONTENT;
        } else if (morale < 0.8f) {
            return Morale.PLEASED;
        } else {
            return Morale.ECSTATIC;
        }
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(double growthRate) {
        this.growthRate = growthRate;
    }

    public int getTurnsUntilGrowth() {
        double diff = Math.ceil(population) - population;
        if (diff == 0.0) {
            diff = 1.0;
        }
        return (int)Math.ceil(diff / growthRate);
    }

    public int getFoodProducers() {
        return foodProducers;
    }

    public void setFoodProducers(int foodProducers) {
        this.foodProducers = foodProducers;
    }

    public int getIndustrialProducers() {
        return industrialProducers;
    }

    public void setIndustrialProducers(int industrialProducers) {
        this.industrialProducers = industrialProducers;
    }

    public int getScienceProducers() {
        return scienceProducers;
    }

    public void setScienceProducers(int scienceProducers) {
        this.scienceProducers = scienceProducers;
    }
    
    public float getNetFoodOutput() {
        return (float)(BASE_FOOD_PRODUCTION + foodProducers) * foodMultiplier - (foodConsumptionRate * (int)population);
    }

    public float getScientificOutput() {
        return (float)(BASE_SCI_PRODUCTION + scienceProducers) * scienceMultiplier;
    }
    
    public float getIndustrialOutput() {
        return (float)(BASE_IND_PRODUCTION + industrialProducers) * industrialMultiplier;
    }

    public Queue<BuildQueueEntry> getBuildQueue() {
        return buildQueue;
    }
    
    public Set<String> getStructures() {
        return structures;
    }
    
    public void updateModifiers() {
        // Default everything...
        scienceMultiplier = 1.0f;
        foodMultiplier = 1.0f;
        industrialMultiplier = 1.0f;
        foodConsumptionRate = 0.3f;
        growthRate = 0.1;
        // TODO: or is morale different from other values in that it changes
        // more slowly, over time?
        morale = 0.5f;

        for (String s : structures) {
            // TODO: update modifiers from structures
        }

        if (getNetFoodOutput() < 0.0f) {
            growthRate = -0.25;
            morale -= 0.25f;
        } else {
            // TODO: add bonus to growth based on food surplus
        }
    }
    
    public void update(State state) {
        produce(state);
        growPopulation();
        updateModifiers();        
    }
    
    /**
     * Grows or shrinks the population based on the current growth rate.
     */
    private void growPopulation() {
        int oldPopulation = (int)population;
        population += growthRate;
        if (population < 1.0) {
            population = 1.0;
        } else if (population > maxPopulation) {
            population = maxPopulation;
        }
        
        int newPopulation = (int)population;
        // Population grew (this works under the assumption that the population
        // will never grow or shrink by more than 1 per turn)
        if (newPopulation > oldPopulation) {
            foodProducers++;
        // Population shrunk
        } else if (oldPopulation > newPopulation) {
            if (foodProducers > 0) {
                foodProducers--;
            } else if (industrialProducers > 0) {
                industrialProducers--;
            } else {
                scienceProducers--;
            }
        }        
    }

    /**
     * Updates top of production queue.
     */
    private void produce(State state) {
        BuildQueueEntry entry = buildQueue.peek();
        if (entry == null) {
            return;
        }
        float remaining = entry.getCost() - getIndustrialOutput();
        if (remaining > 0) {
            entry.setCost(remaining);
        } else {
            buildQueue.remove();
            // add entry to list of structures or create new ship entity
            BuildOption option = state.getBuildTree().getBuildOption(entry.getId());
            if (option instanceof ShipType) {
                Faction ownerFaction = state.getFactions().get(owner);
                Ship ship = new Ship((ShipType)option, ownerFaction);
                ship.setCoordinates(coordinates);        
                state.addUnit(ship);
            } else {
                structures.add(entry.getId());
            }
        }
    }    
    
    public boolean canColonize() {
        return (type != null && owner == null);
    }
    
    public boolean canBuildOutpost() {
        return (type == null && owner == null);
    }
}