package com.afqa123.intergalactic.model;

public interface BuildOption {
    
    String getId();
    
    String getLabel();
    
    String getDetail();
    
    int getCost();
    
    boolean isUnique();
    
    String[] getDependencies();
    
}
