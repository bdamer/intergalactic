package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.math.HexCoordinate;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Goal {

    public enum Type {
        EXPLORE(10),
        BUILD_STRUCTURES(20),
        COLONIZE_SECTOR(50),
        BUILD_STATION(30),
        DESTROY_UNIT(75);
    
        private final int priority;

        private Type(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;
        }
    };
    
    private Type type;
    private HexCoordinate targetSector;
    private String targetUnitId;
    
    Goal() {
        
    }
    
    public Goal(Type type, HexCoordinate targetSector) {
        this.type = type;
        this.targetSector = targetSector;
    }
    
    public Goal(Type type, String targetUnitId) {
        this.type = type;
        this.targetUnitId = targetUnitId;
    }
    
    public Type getType() {
        return type;
    }    
    
    public HexCoordinate getTargetSector() {
        return targetSector;
    }

    public void setTargetSector(HexCoordinate targetSector) {
        this.targetSector = targetSector;
    }

    public String getTargetUnitId() {
        return targetUnitId;
    }

    public void setTargetUnitId(String targetUnitId) {
        this.targetUnitId = targetUnitId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Goal)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            Goal rhs = (Goal)obj;
            boolean res = (this.type == rhs.type);
            if (this.targetSector != null) {
                res &= this.targetSector.equals(rhs.targetSector);
            } else {
                res &= this.targetSector == rhs.targetSector;
            }
            if (this.targetUnitId != null) {
                res &= this.targetUnitId.equals(rhs.targetUnitId);
            } else {
                res &= this.targetUnitId == rhs.targetUnitId;
            }
            return res;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19,263)
                .append(type)
                .append(targetSector)
                .append(targetUnitId)
                .toHashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s", type.name(), targetSector, targetUnitId);
    }
}