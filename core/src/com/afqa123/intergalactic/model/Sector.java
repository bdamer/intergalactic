package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Class representing a single galactic sector.
 */
public final class Sector extends Entity {

    public static final String FLAG_EXPLORED = "explored";
    // Bonus map entries
    public static final String FOOD_BASE = "foodBase";
    public static final String IND_BASE = "indBase";
    public static final String SCI_BASE = "sciBase";
    public static final String FOOD_MOD = "foodMod";
    public static final String IND_MOD = "indMod";
    public static final String SCI_MOD = "sciMod";
    // FX settings
    private static final float TURBULENCE = 1.0f / 100000.0f;
    
    private String name;
    private String owner;
    // Axial coordinates of this sector.
    private HexCoordinate coordinates;
    private StarType type;
    
    // Game stats (updated each turn)
    private double population;
    private int maxPopulation;
    private double growthRate;
    private double morale;
    private double foodConsumptionRate;
    // Members of population allocated to production
    private int foodProducers;
    private int industrialProducers;
    private int scienceProducers;
    // Production multipliers
    private BonusMap bonusMap = new BonusMap();
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
        if (morale < Settings.<Double>get("sectorMoraleRebellious")) {
            return Morale.REBELLIOUS;
        } else if (morale < Settings.<Double>get("sectorMoraleDisgruntled")) {
            return Morale.DISGRUNTLED;
        } else if (morale < Settings.<Double>get("sectorMoraleContent")) {
            return Morale.CONTENT;
        } else if (morale < Settings.<Double>get("sectorMoralePleased")) {
            return Morale.PLEASED;
        } else {
            return Morale.ECSTATIC;
        }
    }

    public double getGrowthRate() {
        return growthRate;
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
    
    public void changeFoodProducers(int change) {
        this.foodProducers += change;
    }

    public void changeIndustrialProducers(int change) {
        this.industrialProducers += change;
    }

    public void changeScienceProducers(int change) {
        this.scienceProducers += change;
    }

    public double getNetFoodOutput() {
        double production = bonusMap.get(FOOD_BASE) + bonusMap.get(FOOD_MOD) * (double)foodProducers;
        double consumption = foodConsumptionRate * (int)population; // round down population
        return production - consumption;
    }

    public double getScientificOutput() {
        return bonusMap.get(SCI_BASE) + bonusMap.get(SCI_MOD) * (double)scienceProducers;
    }
    
    public double getIndustrialOutput() {
        return bonusMap.get(IND_BASE) + bonusMap.get(IND_MOD) * (double)industrialProducers;
    }

    public Queue<BuildQueueEntry> getBuildQueue() {
        return buildQueue;
    }
    
    public Set<String> getStructures() {
        return structures;
    }
        
    public void update(Session session) {
        // Updated colonized planet
        if (population > 0.0) {
            produce(session);
            growPopulation(session);
            updateModifiers(session);        
        // vacant planet without unit can spawn pirates
        } else {
            // TODO: should this be part of the pirate strategy?
            spawnPirate(session);
        }
    }

    public void spawnPirate(Session session) {
        if (type != null && !hasOwner() && session.findUnitInSector(coordinates) == null && 
                Math.random() < Settings.<Double>get("sectorSpawnPirates")) {
            Gdx.app.log(Sector.class.getName(), String.format("Spawning pirate at: %s", coordinates));
            ShipType pirateType = session.getDatabase().getShip("pirate");
            session.createShip(pirateType, coordinates, session.getPirates());
        }
    }
    
    /**
     * Updates top of production queue.
     * 
     * @param session The game session.
     */
    private void produce(Session session) {
        BuildQueueEntry entry = buildQueue.peek();
        if (entry == null) {
            return;
        }
        entry.produce(getIndustrialOutput());
        if (entry.isComplete()) {
            buildQueue.remove();
            // add entry to list of structures or create new ship entity
            BuildOption option = session.getBuildTree().getBuildOption(entry.getId());
            if (option instanceof ShipType) {
                Faction ownerFaction = session.getFactions().get(owner);
                session.createShip((ShipType)option, coordinates, ownerFaction);
            } else {
                structures.add(entry.getId());
            }
            session.trigger(GameEvent.SECTOR_CONSTRUCT, this, entry.getLabel());
        }
    } 
    
    /**
     * Grows or shrinks the population based on the current growth rate.
     */
    private void growPopulation(Session session) {
        int oldPopulation = (int)population;
        population += growthRate;
        if (population < 1.0) {
            population = 1.0;
        } else if (population > maxPopulation) {
            population = maxPopulation;
        }
        
        if (growthRate < 0) {
            session.trigger(GameEvent.SECTOR_STARVATION, this);
        }
        
        int newPopulation = (int)population;
        // Population grew (this works under the assumption that the population
        // will never grow or shrink by more than 1 per turn)
        if (newPopulation > oldPopulation) {
            foodProducers++;
            session.trigger(GameEvent.SECTOR_GROWTH, this);
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
    
    public void updateModifiers(Session session) {
        // Default everything...
        bonusMap.set(FOOD_BASE, Settings.<Double>get("sectorFoodBase"));
        bonusMap.set(IND_BASE, Settings.<Double>get("sectorIndBase"));
        bonusMap.set(SCI_BASE, Settings.<Double>get("sectorSciBase"));
        bonusMap.set(FOOD_MOD, Settings.<Double>get("sectorFoodMod"));
        bonusMap.set(IND_MOD, Settings.<Double>get("sectorIndMod"));
        bonusMap.set(SCI_MOD, Settings.<Double>get("sectorSciMod"));

        foodConsumptionRate = Settings.<Double>get("sectorFoodConsumption");
        
        // Growth rate slows down as population increases [0.09-0.01]
        if (population < 9.0) {
            growthRate = (10.0 - population) / 100.0;
        } else {
            growthRate = 0.01;
        }
        // TODO: or is morale different from other values in that it changes
        // more slowly, over time?
        morale = Settings.get("sectorMorale");

        BuildQueueEntry top = buildQueue.peek();
        if (top != null && top.isInfinite()) {
            // add effects, if any
        }
        
        // merge in bonus modifiers provided by structure
        for (String id : structures) {
            StructureType struc = (StructureType)session.getBuildTree().getBuildOption(id);
            bonusMap.merge(struc.getBonusMap());
        }

        if (getNetFoodOutput() < 0.0f) {
            growthRate = Settings.<Double>get("sectorStarvationGrowthRate");
            morale += Settings.<Double>get("sectorStarvationMorale");
        } else {
            // TODO: add bonus to growth based on food surplus
        }
    }
    
    public boolean canColonize() {
        return (type != null && owner == null);
    }
    
    public boolean canBuildOutpost() {
        return (type == null && owner == null);
    }
    
    /**
     * Returns true if this sector contains a colony of a given faction.
     * 
     * @param faction The faction.
     * @return True if this sector contains a colony of the faction.
     */
    public boolean isColony(Faction faction) {
        return (type != null && faction.getName().equals(owner));
    }
    
    public boolean isIdle() {
        return buildQueue.size() == 0;
    }
    
    public boolean hasOwner() {
        return owner != null;
    }
    
    public void colonize(Session session, String owner) {
        this.owner = owner;
        this.population = Settings.get("sectorInitialPopulation");
        this.foodProducers = Settings.get("sectorInitialFoodProducers");
        this.industrialProducers = Settings.get("sectorInitialIndProducers");
        this.scienceProducers = Settings.get("sectorInitialSciProducers");
        // TODO: compute automatically based on number of terraformed planets
        this.maxPopulation = 10;
        setFlag(Sector.FLAG_EXPLORED);
        updateModifiers(session);
    }
}