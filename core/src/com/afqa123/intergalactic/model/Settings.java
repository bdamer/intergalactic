package com.afqa123.intergalactic.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class Settings implements Json.Serializable {

    private static Settings INSTANCE;
    private final Map<String,Object> map = new HashMap<>();
    
    public static void initialize(Settings settings) {
        INSTANCE = settings;
    }
    
    public static <T> T get(String name) {
        return (T)INSTANCE.map.get(name);
    }
    
    public static void set(String name, Object value) {
        INSTANCE.map.put(name, value);
    }

    @Override
    public void write(Json json) {
        json.writeValue("values", map);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        map.putAll(json.readValue("values", HashMap.class, jv));
    }
}