package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Sector;
import java.util.Set;

public class BuildPlan {

    private String name;
    private String[] order;
    
    BuildPlan() {
        
    }
    
    public BuildPlan(String name, String[] order) {
        this.name = name;
        this.order = order;
    }
    
    public String getNextStructure(Sector sector) {
        Set<String> built = sector.getStructures();
        for (String s : order) {
            if (!built.contains(s)) {
                return s;
            }
        }
        return null;
    }
}