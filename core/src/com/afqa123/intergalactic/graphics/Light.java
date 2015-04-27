package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface Light {
    
    String LIGHT_POS = "u_lightPos";
    String LIGHT_COLOR = "u_lightColor";
    String LIGHT_AMBIENT  = "u_lightAmbient";
    
    void bind(ShaderProgram sp);
}