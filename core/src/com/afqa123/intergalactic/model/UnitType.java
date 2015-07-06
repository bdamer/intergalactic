package com.afqa123.intergalactic.model;

public interface UnitType {

    enum Action {
        BUILD_STATION, COLONIZE, INTERCEPT
    };

    String getId();
    
    String getLabel();

    Action[] getActions();
    
}