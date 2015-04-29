package com.afqa123.intergalactic.math;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Hex {
    
    public static final float SIZE = 1.0f;
    public static final float HALF_SIZE = SIZE / 2.0f;
    public static final float HEIGHT = (float)Math.sqrt(SIZE * SIZE - HALF_SIZE * HALF_SIZE);    
    public static final float SQRT_THREE = (float)Math.sqrt(3.0f);    

    /**
     * Transforms axial coordinates to world coordinates. 
     * 
     * @param coords The axial coordinates.
     * @return The world coordinates.
     */
    public static Vector3 axialToWorld(final Vector2 coords) {
        return new Vector3(
            SIZE * SQRT_THREE * (coords.x + coords.y / 2.0f),
            0.0f,
            SIZE * 3.0f / 2.0f * coords.y);
    }
    
    /**
     * Transform world coordinates to axial coordinates.
     * 
     * @param world The world coordinates.
     * @return The axial coordinates.
     */
    public static Vector2 worldToAxial(Vector3 world) {
        return new Vector2(
            (world.x * SQRT_THREE / 3.0f - world.z / 3.0f) / SIZE,
            world.z * 2.0f / 3.0f / SIZE);
    }
    
    /**
     * Rounds a set of cube coordinates.
     * 
     * @param cubeCoords The cube coordinates.
     */
    public static void round(Vector3 cubeCoords)
    {
        float rx = (float)Math.round(cubeCoords.x);
        float ry = (float)Math.round(cubeCoords.y);
        float rz = (float)Math.round(cubeCoords.z);

        float xd = Math.abs(rx - cubeCoords.x);
        float yd = Math.abs(ry - cubeCoords.y);
        float zd = Math.abs(rz - cubeCoords.z);

        if (xd > yd && xd > zd)
        {
            rx = -ry - rz;
        }
        else if (yd > zd)
        {
            ry = -rx - rz;
        }
        else
        {
            rz = -rx - ry;
        }

        cubeCoords.x = rx;
        cubeCoords.y = ry;
        cubeCoords.z = rz;
    }    
}