package com.afqa123.intergalactic.data;

import java.util.ArrayList;
import java.util.List;

public class Faction {

    private final String name;
    private final boolean player;
    private final List<Sector> sectors = new ArrayList<>();
    private final FactionMap map;
    private final List<Ship> ships = new ArrayList<>();
    
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
    
    public List<Ship> getShips() {
        return ships;
    }
}