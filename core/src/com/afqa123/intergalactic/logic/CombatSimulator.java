package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Unit;

public class CombatSimulator {

    public enum CombatResult { 
        VICTORY,
        DEFEAT,
        DRAW
    };
    
    public CombatResult simulate(Unit attacker, Unit defender) {
        // TODO: implement
        return CombatResult.VICTORY;
    }    
}