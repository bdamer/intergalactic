package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.logic.strategy.SimpleStrategy;
import com.afqa123.intergalactic.logic.strategy.Strategy;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class Faction implements Json.Serializable {

    private String name;
    private boolean player;
    private FactionMap map;
    private Color color;
    private Strategy strategy;
    private final List<Ship> ships = new ArrayList<>();

    Faction() {
        // required for serialization
    }
    
    public Faction(String name, Color color, boolean player, Galaxy galaxy) {
        this.name = name;
        this.color = color;
        this.player = player;
        this.map = new FactionMap(galaxy);
        this.strategy = new SimpleStrategy(name);
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
        strategy = json.readValue("strategy", SimpleStrategy.class, jv);
    }
}