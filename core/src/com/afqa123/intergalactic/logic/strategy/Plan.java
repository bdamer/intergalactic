package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.model.Session;

public interface Plan {

    public enum Status {
        BLOCKED,
        ACTIVE,
        COMPLETE,
        INVALID
    };

    Status update(Session state, SimpleStrategy.FactionState fs);
    
    Goal getGoal();
}