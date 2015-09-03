package com.afqa123.intergalactic.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BonusMap implements Json.Serializable {
    
    private final Map<String,Double> map = new HashMap<>();
    
    public BonusMap() {
        
    }
    
    /**
     * Merges a {@code BonusMap} into this map.
     * 
     * @param bm The {@code BonusMap} to merge.
     */
    public void merge(BonusMap bm) {
        for (String key : bm.keySet()) {
            merge(key, bm.get(key));
        }
    }

    /**
     * Sets a bonus value in this map.
     * 
     * @param key The bonus name.
     * @param value The bonus value.
     */
    public void set(String key, double value) {
        map.put(key, value);
    }
    
    /**
     * Returns a bonus value from this map.
     * 
     * @param key The bonus name.
     * @return The bonus value or 0.
     */
    public double get(String key) {
        Double val = map.get(key);
        if (val != null) {
            return map.get(key);
        } else {
            return 0.0;
        }
    }
    
    /**
     * Merges a single bonus value into the map.
     * 
     * @param key The bonus name.
     * @param value The bonus value.
     */
    public void merge(String key, double value) {
        Double cur = map.get(key);
        if (cur != null) {
            map.put(key, cur + value);
        } else {
            map.put(key, value);
        }
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public void write(Json json) {
        for (String key : map.keySet()) {
            json.writeValue(key, map.get(key));
        }
    }

    @Override
    public void read(Json json, JsonValue jv) {
        for (JsonValue prop : jv) {
            map.put(prop.name, prop.asDouble());
        }        
    }
}