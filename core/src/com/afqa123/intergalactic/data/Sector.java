package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class representing a single galactic sector.
 */
public final class Sector {

    // TODO: review
    private static final int BASE_FOOD_PRODUCTION = 0;
    private static final int BASE_IND_PRODUCTION = 0;
    private static final int BASE_SCI_PRODUCTION = 0;
    
    public enum StarCategory {
        BLUE,       // gigantic
        WHITE,      // large
        YELLOW,     // average
        ORANGE,     // small
        RED         // dwarf
    };

    public enum Morale {
        ECSTATIC("Ecstatic"),
        PLEASED("Pleased"),
        CONTENT("Content"),
        DISGRUNTLED("Disgruntled"),
        REBELLIOUS("Rebellious");
        
        private final String label;
        
        private Morale(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    };
    
    private final String name;
    private Faction owner;
    
    // Axial coordinates of this sector.
    private final HexCoordinate coordinates;
    private final StarCategory category;
    
    // Game stats (updated each turn)
    private float population;
    private int maxPopulation;
    private float morale;
    private float growthRate;
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
    private List<Structure> structures;
    private Queue<BuildQueueEntry> buildQueue;
    
    // Rendering properties
    // Base color / material
    // TODO: review - currently not used
    private final Vector3 material;
    // Scale of system on map
    private final float scale;
    // Color gradient
    private final float gradient;
    // Surface turbulence
    private final float turbulence;
    // seed value to animate surface
    private final long seed;
        
    public Sector(String name, HexCoordinate coordinates, StarCategory category) {
        this.name = name;
        this.coordinates = coordinates;
        this.category = category;
        this.seed = System.currentTimeMillis() - (long)(Math.random() * 1000000.0);

        this.population = 0;
        // TODO: this is based on the number of terraformed planets in this sector
        this.maxPopulation = 0;
        this.foodProducers = 0;
        this.structures = new ArrayList<>();
        this.buildQueue = new LinkedList<>();
        computerModifiers();
        
        if (category != null) {
            switch (category) {
                case BLUE:
                    scale = 1.0f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(0.0f, 0.0f, 1.0f);
                    gradient = 20.0f / 32.0f;
                    break;
                case WHITE:
                    scale = 0.8f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(1.0f, 1.0f, 1.0f);
                    gradient = 15.0f / 32.0f;
                    break;
                case YELLOW:
                    scale = 0.6f;
                    turbulence = 1.0f / 35000.0f;
                    material = new Vector3(1.0f, 1.0f, 0.0f);
                    gradient = 10.0f / 32.0f;
                    break;
                case ORANGE:
                    scale = 0.5f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(1.0f, 0.35f, 0.0f);
                    gradient = 5.0f / 32.0f;
                    break;
                case RED:
                    scale = 0.3f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(1.0f, 0.0f, 0.0f);
                    gradient = 0.0f / 32.0f;
                    break;
                default:
                    throw new RuntimeException("Unsupported sector category: " + category);
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

    public StarCategory getCategory() {
        return category;
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

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }    
    
    public float getPopulation() {
        return population;
    }

    public void setPopulation(float population) {
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

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
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
    
    public void computerModifiers() {
        // Default everything...
        scienceMultiplier = 1.0f;
        foodMultiplier = 1.0f;
        industrialMultiplier = 1.0f;
        foodConsumptionRate = 1.0f;
        growthRate = 0.25f;
        // TODO: or is morale different from other values in that it changes
        // more slowly, over time?
        morale = 0.5f;

        for (Structure s : structures) {
            // TODO: update modifiers from structures
        }

        if (getNetFoodOutput() < 0.0f) {
            growthRate = -0.25f;
            morale -= 0.25f;
        } else {
            // TODO: add bonus to growth based on food surplus
        }
    }
    
    /**
     * Grows or shrinks the population based on the current growth rate.
     */
    public void growPopulation() {
        int oldPopulation = (int)population;
        population += population * growthRate;
        if (population < 1.0f) {
            population = 1.0f;
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
    public void produce() {
        BuildQueueEntry entry = buildQueue.peek();
        if (entry == null) {
            return;
        }
        float remaining = entry.getCost() - getIndustrialOutput();
        if (remaining > 0) {
            entry.setCost(remaining);
        } else {
            buildQueue.remove();
            // TODO: add entry to list of structures or create new ship entity
        }
    }    
}