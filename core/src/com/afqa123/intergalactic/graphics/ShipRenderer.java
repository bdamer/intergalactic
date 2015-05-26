package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.data.Ship;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;

public class ShipRenderer implements Disposable {

    private static final float SIZE = 0.25f;
    private static final Vector3 OFFSET = new Vector3(0.0f, 0.2f, 0.0f);
    private final ShaderProgram sp;
    private final Mesh mesh;        
    
    public ShipRenderer() {
        sp = ShaderFactory.buildShader("shaders/default.vsh", "shaders/default.fsh");
        mesh = new MeshBuilder().buildCube();
    }
    
    public void render(Camera cam, List<Ship> ships) {
        Matrix4 mvp = new Matrix4();
        Matrix4 model = new Matrix4();
        
        sp.begin();

        for (Ship ship : ships) {
            Vector3 pos = ship.getCoordinates().toWorld();
            pos.add(OFFSET);
            if (!cam.frustum.sphereInFrustum(pos, SIZE)) {
                continue;
            }            
            
            model.setToTranslationAndScaling(pos.x, pos.y, pos.z, SIZE, SIZE, SIZE);
            mvp.set(cam.combined);
            mvp.mul(model);

            sp.setUniformMatrix("u_mvp", mvp);
            sp.setUniformf("u_color", Color.WHITE);
            mesh.render(sp, GL20.GL_TRIANGLES);
        }
        
        sp.end();
    }
    
    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
    }
}
