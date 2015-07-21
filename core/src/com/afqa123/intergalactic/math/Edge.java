package com.afqa123.intergalactic.math;

import com.badlogic.gdx.math.Vector3;

public class Edge {
 
    private final Vector3 from;
    private final Vector3 to;

    public Edge(Vector3 from, Vector3 to) {
        this.from = from;
        this.to = to;
    }
    
    public Vector3 getFrom() {
        return from;
    }
    
    public Vector3 getTo() {
        return to;
    }    
}