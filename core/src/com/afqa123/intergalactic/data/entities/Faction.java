package com.afqa123.intergalactic.data.entities;

import com.afqa123.intergalactic.data.FactionMap;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.data.entities.Unit;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class Faction {

    private final String name;
    private final Color color;
    private final boolean player;
    private final FactionMap map;
    private final List<Sector> sectors = new ArrayList<>();
    private final List<Unit> units = new ArrayList<>();
    
    public Faction(String name, Color color, boolean player, Galaxy galaxy) {
        this.name = name;
        this.color = color;
        this.player = player;
        this.map = new FactionMap(this, galaxy.getRadius());
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isPlayer() {
        return player;
    }    
    
    public List<Sector> getSectors() {
        return sectors;
    }
    
    public FactionMap getMap() {
        return map;
    }
    
    public List<Unit> getUnits() {
        return units;
    }

    public void addColony(Sector sector) {
        sector.setOwner(this);
        sector.setPopulation(2.0f);
        sector.setFoodProducers(1);
        sector.setIndustrialProducers(1);
        // TODO: compute automatically based on number of terraformed planets
        sector.setMaxPopulation(10);
        sector.computerModifiers();
        sectors.add(sector);
        map.addColony(sector);
        map.update();
    }
    
    public void addUnit(Unit unit) {
        units.add(unit);   
        map.explore(unit.getCoordinates(), unit.getScanRange());
    }
    
    public void removeUnit(Unit unit) {
        units.remove(unit);
    }
    
    public Unit findUnitInSector(HexCoordinate c) {
        for (Unit u : units) {
            if (u.getCoordinates().equals(c)) {
                return u;
            }
        }
        return null;
    }
}