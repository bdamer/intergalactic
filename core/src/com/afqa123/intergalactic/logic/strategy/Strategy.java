package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.model.Session;

public interface Strategy {
        
    /**
     * Computes the next turn.
     * 
     * @param session The game session.
     */
    void nextTurn(Session session);

}