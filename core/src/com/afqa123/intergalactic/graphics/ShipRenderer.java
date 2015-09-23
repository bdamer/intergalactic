package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Ship;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: clean up naming -> unit renderer?
public class ShipRenderer implements Disposable {

    private static final float SIZE = 0.25f;
    private static final Vector3 OFFSET = new Vector3(0.0f, 0.2f, 0.0f);
    private final ShaderProgram sp;
    private final Map<String,Mesh> meshes;
    
    public ShipRenderer() {
        sp = ShaderFactory.buildShader("shaders/sc_unit.vsh", "shaders/sc_unit.fsh");
        JsonValue model = Assets.<JsonValue>get("meshes/raider_pnt.mesh");
        meshes = new HashMap<>();
        meshes.put("scout", new MeshBuilder().load(model, new float[] { 0.25f, 0.0f, 0.3125f, 0.0625f }));
        meshes.put("raider", new MeshBuilder().load(model, new float[] { 0.25f, 0.0625f, 0.3125f, 0.125f }));
        meshes.put("outpost", new MeshBuilder().load(model, new float[] { 0.3125f, 0.0f, 0.375f, 0.0625f }));
        meshes.put("colony_ship", new MeshBuilder().load(model, new float[] { 0.3125f, 0.0625f, 0.375f, 0.125f }));
        // uses raider mesh
        meshes.put("pirate", new MeshBuilder().load(model, new float[] { 0.25f, 0.0625f, 0.3125f, 0.125f }));
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
            sp.setUniformf("u_color", ship.getOwner().getColor());

            Mesh mesh = meshes.get(ship.getType());
            mesh.render(sp, GL20.GL_TRIANGLES);
        }
        
        sp.end();
    }
    
    @Override
    public void dispose() {
        for (Mesh m : meshes.values()) {
            m.dispose();
        }
        sp.dispose();
    }
}