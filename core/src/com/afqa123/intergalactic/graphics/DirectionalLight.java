package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

/**
 * Directional light source.
 */
public class DirectionalLight implements Light {

    // Direction of the light
    private Vector3 direction;
    // Color of the light
    private Vector3 color;
    // Ambient coefficient of the light
    private float ambientCoefficient;
    
    public DirectionalLight(Vector3 direction, Vector3 color, float ambientCoefficient) {
        this.direction = direction.nor();
        this.color = color;
        this.ambientCoefficient = ambientCoefficient;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction.nor();
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public float getAmbientCoefficient() {
        return ambientCoefficient;
    }

    public void setAmbientCoefficient(float ambientCoefficient) {
        this.ambientCoefficient = ambientCoefficient;
    }
    
    @Override
    public void bind(ShaderProgram sp) {
        sp.setUniformf(LIGHT_POS, direction);
        sp.setUniformf(LIGHT_COLOR, color);
        sp.setUniformf(LIGHT_AMBIENT, ambientCoefficient);
    }    
}