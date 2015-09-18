package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.logic.strategy.Strategy;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class Faction implements Json.Serializable {

    public static final String PLAYER_FACTION = "player";
    public static final String PIRATE_FACTION = "pirates";
    private String name;
    private boolean player;
    private FactionMap map;
    private Color color;
    private Strategy strategy;
    private final List<Ship> ships = new ArrayList<>();

    Faction() {
        // required for serialization
    }
    
    public Faction(String name, Color color, Galaxy galaxy, Strategy strategy) {
        this.name = name;
        this.color = color;
        this.map = new FactionMap(galaxy);
        this.strategy = strategy;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isPlayer() {
        return PLAYER_FACTION.equals(name);
    }    

    public boolean isPirates() {
        return PIRATE_FACTION.equals(name);
    }    
    
    public FactionMap getMap() {
        return map;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }
    
    public List<Ship> getShips() {
        return ships;
    }
        
    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("player", player);
        json.writeValue("map", map);
        json.writeValue("color", color);
        json.writeValue("strategy", strategy);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        name = json.readValue("name", String.class, jv);
        player = json.readValue("player", Boolean.class, jv);
        map = json.readValue("map", FactionMap.class, jv);
        color = json.readValue("color", Color.class, jv);
        strategy = json.readValue("strategy", Strategy.class, jv);
    }
}