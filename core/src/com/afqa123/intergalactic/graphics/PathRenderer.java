package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.util.Path.PathStep;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class PathRenderer implements Disposable {

    private final static float SCALE = 0.15f;
    private final static float OFFSET = 0.5f;
    private final ShaderProgram sp;
    private final Mesh mesh;
    private final Color validColor = new Color(0.0f, 0.2f, 1.0f, 0.75f);
    private final Color invalidColor = new Color(0.8f, 0.0f, 0.0f, 0.8f);
    
    public PathRenderer() {
        sp = ShaderFactory.buildShader("shaders/sc_default.vsh", "shaders/sc_default.fsh");
        mesh = new MeshBuilder().buildSquare();
    }
    
    public void render(Camera camera, Unit unit) {
        Path path = unit.getPath();
        if (path == null) {
            return;
        }
        
        float points = unit.getMovementPoints();
        
        Matrix4 model = new Matrix4();
        Matrix4 mvp = new Matrix4();
        
        sp.begin();
        for (PathStep step : path) {
            if (step.invalid) {
                break;
            }
            Vector3 world = step.coordinate.toWorld();
            model.setToTranslationAndScaling(world.x, OFFSET, world.z, SCALE, SCALE, SCALE);
            mvp.set(camera.combined);
            mvp.mul(model);
            sp.setUniformMatrix("u_mvp", mvp);
            sp.setUniformf("u_color", (points >= step.cost ? validColor : invalidColor));
            mesh.render(sp, GL20.GL_TRIANGLES);
        }
        sp.end();
    }

    @Override
    public void dispose() {
        sp.dispose();
        mesh.dispose();
    }   
}