package com.afqa123.intergalactic.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic game entity.
 */
public abstract class Entity {

    protected final Map<String,Boolean> flags = new HashMap<>();
    
    public void setFlag(String id) {
        flags.put(id, true);
    }
    
    public void unsetFlag(String id) {
        flags.put(id, false);
    }

    public boolean getFlag(String id) {
        return flags.containsKey(id) && flags.get(id);
    }   
}