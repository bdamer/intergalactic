package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;

public class Station extends Entity implements Unit, Json.Serializable {

    private String id;
    private StationType type;
    private Faction owner;
    private HexCoordinate coordinates;
    private int construction;
    private float health;
    
    // TODO: fixme - only needed during deserialization
    private String typeName;
    private String ownerName;    

    Station() {
        // required for serialization
    }
    
    /**
     * Creates a new station of a given type. Unit should only be created using 
     * the factory methods provided by {@code Session}.
     * 
     * @param id The station id
     * @param type The station type.
     * @param coordinates The initial coordinates.
     * @param owner The owner faction.
     */    
    Station(String id, StationType type, HexCoordinate coordinates, Faction owner) {
        this.id = id;
        this.type = type;
        this.coordinates = coordinates;
        this.owner = owner;
        this.ownerName = owner.getName();
        this.health = type.getHealth();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getType() {
        return type.getId();
    }
    
    @Override
    public Faction getOwner() {
        return owner;
    }

    @Override
    public HexCoordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(HexCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int getScanRange() {
        return type.getScanRange();
    }

    @Override
    public float getBaseAttack() {
        return 0.0f;
    }
    
    @Override
    public float getBaseDefense() {
        return type.getDefense() * getPower();
    }
    
    @Override
    public float getPower() {
        float power = health / type.getHealth();
        return Math.max(power, 0.1f);
    }
    
    @Override
    public void applyDamage(float damage) {
        health -= damage;
    }

    @Override
    public float getHealth() {
        return health;
    }
    
    @Override
    public void update(Session session) {
        if (construction < type.getCost()) {
            construction++;
            if (construction == type.getCost()) {
                // increase faction range once construction is done
                owner.getMap().explore(coordinates, type.getScanRange());
                owner.getMap().addRange(coordinates);        
                session.trigger(GameEvent.STATION_CONSTRUCT, this);
            }
        }
    }
    
    @Override
    public void refresh(Session state) {
        // needed to re-initialize after deserialization
        if (type == null) {
            type = state.getDatabase().getStation(typeName);
        }
        if (owner == null) {
            owner = state.getFactions().get(ownerName);
        }
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("type", type.getId());
        json.writeValue("owner", owner.getName());
        json.writeValue("coordinates", coordinates);
        json.writeValue("construction", construction);
        json.writeValue("flags", flags);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        id = json.readValue("id", String.class, jv);
        typeName = json.readValue("type", String.class, jv);
        ownerName = json.readValue("owner", String.class, jv);
        coordinates = json.readValue("coordinates", HexCoordinate.class, jv);
        construction = json.readValue("construction", Integer.class, jv);
        flags.putAll(json.readValue("flags", HashMap.class, jv));
    }
}