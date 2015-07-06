package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.model.Session;

public interface Strategy {
        
    void nextTurn(Session state);

}