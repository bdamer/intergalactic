package com.afqa123.intergalactic.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Faction implements Json.Serializable {

    private String name;
    private boolean player;
    private FactionMap map;
    private Color color;

    Faction() {
        // required for serialization
    }
    
    public Faction(String name, Color color, boolean player, Galaxy galaxy) {
        this.name = name;
        this.color = color;
        this.player = player;
        this.map = new FactionMap(galaxy.getRadius());
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
    
    public FactionMap getMap() {
        return map;
    }
    
    public void addColony(Sector sector) {
        sector.setOwner(name);
        sector.setPopulation(2.0f);
        sector.setFoodProducers(1);
        sector.setIndustrialProducers(1);
        // TODO: compute automatically based on number of terraformed planets
        sector.setMaxPopulation(10);
        sector.updateModifiers();
        map.addRange(sector.getCoordinates());
    }
    
    public Station addStation(Sector sector, StationType type) {
        Station station = new Station(type, this);        
        station.setCoordinates(sector.getCoordinates());
        map.addRange(sector.getCoordinates());
        return station;
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("player", player);
        json.writeValue("map", map);
        json.writeValue("color", color);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        name = json.readValue("name", String.class, jv);
        player = json.readValue("player", Boolean.class, jv);
        map = json.readValue("map", FactionMap.class, jv);
        color = json.readValue("color", Color.class, jv);
    }
}