/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
