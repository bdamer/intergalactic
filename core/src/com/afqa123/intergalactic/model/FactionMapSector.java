package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;

public class FactionMapSector { 
    
    private HexCoordinate coordinate;
    private Range range;
    private SectorStatus status;

    FactionMapSector() {
        // required for serialization
    }
    
    public FactionMapSector(HexCoordinate coordinate) {
        this.coordinate = coordinate;
        range = null;
        status = SectorStatus.UNKNOWN;
    }
    
    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
    
    /**
     * Sets range only if the new value is "better" than the previous value.
     * 
     * @param r The new range.
     */
    public void addRange(Range r) {
        if (range == null || range.ordinal() > r.ordinal()) {
            range = r;
        }
    }

    public SectorStatus getStatus() {
        return status;
    }

    public void setStatus(SectorStatus status) {
        this.status = status;
    }

    /**
     * Sets status only if the new value is "better" than the previous value.
     * 
     * @param s The new status.
     */
    public void addStatus(SectorStatus s) {
        if (status == null || status.ordinal() < s.ordinal()) {
            status = s;
        }
    }
    
    public HexCoordinate getCoordinate() {
        return coordinate;
    }    
}