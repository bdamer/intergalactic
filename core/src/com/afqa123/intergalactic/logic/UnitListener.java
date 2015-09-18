package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Unit;

/**
 * Listener to unit events.
 */
public interface UnitListener {
    
    void unitCreated(Unit u);
    
    void unitDestroyed(Unit u);
    
}
