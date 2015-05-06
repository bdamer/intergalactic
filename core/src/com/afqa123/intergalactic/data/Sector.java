package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.math.Vector3;

/**
 * Class representing a single galactic sector.
 */
public class Sector {

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
    
    // Axial coordinates of this sector.
    private final HexCoordinate coordinates;
    private final StarCategory category;
    
    // Game stats (updated each turn)
    private float population;
    private int maxPopulation;
    private float morale;
    private float growthRate;
    // Members of population allocated to production
    private int foodProducers;
    private int industrialProducers;
    private int scienceProducers;    
    
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
    
    // Tile flags as relevant to player
    // TODO: do we have to determine this dynamically relative to each player?
    private boolean visible = true;
    private boolean shortRange;
    private boolean longRange;
    
    public Sector(String name, HexCoordinate coordinates, StarCategory category) {
        this.name = name;
        this.coordinates = coordinates;
        this.category = category;
        this.seed = System.currentTimeMillis() - (long)(Math.random() * 1000000.0);

        this.growthRate = 0.5f;
        this.population = 1;
        this.maxPopulation = 10;
        this.morale = 0.5f;
        
        if (category != null) {
            switch (category) {
                case BLUE:
                    scale = 0.4f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(0.0f, 0.0f, 1.0f);
                    gradient = 20.0f / 32.0f;
                    break;
                case WHITE:
                    scale = 0.3f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(1.0f, 1.0f, 1.0f);
                    gradient = 15.0f / 32.0f;
                    break;
                case YELLOW:
                    scale = 0.25f;
                    turbulence = 1.0f / 35000.0f;
                    material = new Vector3(1.0f, 1.0f, 0.0f);
                    gradient = 10.0f / 32.0f;
                    break;
                case ORANGE:
                    scale = 0.2f;
                    turbulence = 1.0f / 30000.0f;
                    material = new Vector3(1.0f, 0.35f, 0.0f);
                    gradient = 5.0f / 32.0f;
                    break;
                case RED:
                    scale = 0.125f;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isShortRange() {
        return shortRange;
    }

    public void setShortRange(boolean shortRange) {
        this.shortRange = shortRange;
    }

    public boolean isLongRange() {
        return longRange;
    }

    public void setLongRange(boolean longRange) {
        this.longRange = longRange;
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
}