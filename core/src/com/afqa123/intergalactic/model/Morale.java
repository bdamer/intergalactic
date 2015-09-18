package com.afqa123.intergalactic.model;

public enum Morale {

    ECSTATIC("Ecstatic"),
    PLEASED("Pleased"),
    CONTENT("Content"),
    DISGRUNTLED("Disgruntled"),
    REBELLIOUS("Rebellious");

    private final String label;

    private Morale(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
