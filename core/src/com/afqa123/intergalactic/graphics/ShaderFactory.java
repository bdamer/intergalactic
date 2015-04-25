package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderFactory {

    public static ShaderProgram buildShader(String vertFile, String fragFile) {
        Gdx.app.log(ShaderFactory.class.getName(),
                String.format("Building shader from: %s %s", vertFile, fragFile));
        
        String vs = Assets.get(vertFile);
        Gdx.app.debug(ShaderFactory.class.getName(), 
                String.format("Vertex shader:\n%s", vs));       
        String fs = Assets.get(fragFile);
        Gdx.app.debug(ShaderFactory.class.getName(), 
                String.format("Fragment shader:\n%s", fs));       
        ShaderProgram res = new ShaderProgram(vs, fs);     
        if (!res.isCompiled()) {
            Gdx.app.log(ShaderFactory.class.getName(), 
                    String.format("Failed to compile shader: %s", res.getLog()));
            throw new RuntimeException("Could not compile shader.");
        }
        return res;
    }    
}
