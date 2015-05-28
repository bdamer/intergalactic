package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import java.util.ArrayList;
import java.util.List;

public class Faction {

    private final String name;
    private final boolean player;
    private final FactionMap map;
    private final List<Sector> sectors = new ArrayList<>();
    private final List<Unit> units = new ArrayList<>();
    
    public Faction(String name, boolean player, Galaxy galaxy) {
        this.name = name;
        this.player = player;
        this.map = new FactionMap(this, galaxy.getRadius());
    }

    public String getName() {
        return name;
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
        sectors.add(sector);
        map.addColony(sector);
    }
    
    public void addUnit(Unit unit) {
        units.add(unit);   
        map.explore(unit.getCoordinates(), unit.getScanRange());
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