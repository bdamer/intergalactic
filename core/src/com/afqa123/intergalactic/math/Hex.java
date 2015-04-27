package com.afqa123.intergalactic.math;

import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Size;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Hex {
    
    public static final float SIZE = 1.0f;
    public static final float HALF_SIZE = SIZE / 2.0f;
    public static final float HEIGHT = (float)Math.sqrt(SIZE * SIZE - HALF_SIZE * HALF_SIZE);    
    public static final float SQRT_THREE = (float)Math.sqrt(3.0f);    

    /**
     * Transforms axis coordinates to pixel coordinates. 
     * 
     * @param coords The axial coordinates
     * @return The world coordinates.
     */
    public static Vector3 axialToWorld(final Vector2 coords) {
        return new Vector3(
            SIZE * SQRT_THREE * (coords.x + coords.y / 2.0f),
            0.0f,
            SIZE * 3.0f / 2.0f * coords.y);
    }
}