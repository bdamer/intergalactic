package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.model.Station;
import com.afqa123.intergalactic.model.Unit;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;

// TODO: clean up naming -> unit renderer?
public class StationRenderer implements Disposable {

    private static final float SIZE = 0.5f;
    private static final Vector3 OFFSET = new Vector3(0.0f, -0.5f, 0.0f);
    private final ShaderProgram sp;
    private final Mesh mesh;        
    
    public StationRenderer() {
        sp = ShaderFactory.buildShader("shaders/sc_default.vsh", "shaders/sc_default.fsh");
        mesh = new MeshBuilder().buildCube();
    }
    
    public void render(Camera cam, List<Station> stations) {
        Matrix4 mvp = new Matrix4();
        Matrix4 model = new Matrix4();
        
        sp.begin();

        for (Station station : stations) {
            Vector3 pos = station.getCoordinates().toWorld();
            pos.add(OFFSET);
            if (!cam.frustum.sphereInFrustum(pos, SIZE)) {
                continue;
            }            
            
            model.setToTranslationAndScaling(pos.x, pos.y, pos.z, SIZE, SIZE, SIZE);
            mvp.set(cam.combined);
            mvp.mul(model);

            sp.setUniformMatrix("u_mvp", mvp);
            sp.setUniformf("u_color", station.getOwner().getColor());
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