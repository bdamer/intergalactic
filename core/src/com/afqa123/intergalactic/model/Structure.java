package com.afqa123.intergalactic.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Structure implements BuildOption {

    private final String id;
    private final String label;
    private final String detail;
    private final int cost;
    private final String[] dependencies;

    public Structure(JsonValue json) {
        this.id = json.getString("id");
        this.label = json.getString("label");
        this.detail = json.getString("detail");
        this.cost = json.getInt("cost");
        JsonValue depends = json.get("depends");
        this.dependencies = new String[depends.size];
        for (int i = 0; i < depends.size; i++) {
            dependencies[i] = depends.getString(i);
        }
    }
    
    public Structure(String id, String label, String detail, int cost, String[] dependencies) {
        this.id = id;
        this.label = label;
        this.detail = detail;
        this.cost = cost;
        this.dependencies = dependencies;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public boolean isUnique() {
        return true;
    }
    
    @Override
    public String[] getDependencies() {
        return dependencies;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    public void writeJson(Json json) {
        json.writeValue("id", id);
        json.writeValue("label", label);
        json.writeValue("detail", detail);
        json.writeValue("cost", cost);
        json.writeValue("dependencies", dependencies);
    }
}