package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.UnitType.Action;
import com.afqa123.intergalactic.util.Path;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Station implements Unit, Json.Serializable {

    private String id;
    private StationType type;
    private Faction owner;
    private HexCoordinate coordinates;

    // TODO: fixme - only needed during deserialization
    private String typeName;
    private String ownerName;    

    Station() {
        
    }
    
    public Station(StationType type, Faction owner) {
        this.type = type;
        this.owner = owner;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public float getMovementPoints() {
        return 0.0f;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public HexCoordinate getTarget() {
        return null;
    }

    @Override
    public void selectTarget(HexCoordinate target) {
        
    }

    @Override
    public void move() {
        
    }

    @Override
    public boolean isReadyForStep() {
        return true;
    }

    @Override
    public void step() {
        // nothing to do?
    }

    @Override
    public boolean canPerformAction(Action action) {
        for (Action a : type.getActions()) {
            if (a == action) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean colonizeSector(Session session) {
        return false;
    }

    @Override
    public boolean buildStation(Session session) {
        return false;
    }    
    
    @Override
    public void wake() {
        
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
        json.writeValue("type", type.getId());
        json.writeValue("owner", owner.getName());
        json.writeValue("coordinates", coordinates);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        typeName = json.readValue("type", String.class, jv);
        ownerName = json.readValue("owner", String.class, jv);
        coordinates = json.readValue("coordinates", HexCoordinate.class, jv);
    }
}