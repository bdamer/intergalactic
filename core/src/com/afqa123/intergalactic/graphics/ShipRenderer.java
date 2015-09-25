package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Ship;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: clean up naming -> unit renderer?
public class ShipRenderer implements Disposable {

    private static final float SIZE = 0.25f;
    private static final Vector3 OFFSET = new Vector3(0.0f, 0.2f, 0.0f);
    private final ShaderProgram sp;
    private final ShaderProgram defaultSp;
    private final Map<String,Mesh> meshes;
    private final Mesh bar;
    
    public ShipRenderer() {
        sp = ShaderFactory.buildShader("shaders/sc_unit.vsh", "shaders/sc_unit.fsh");
        defaultSp = ShaderFactory.buildShader("shaders/sc_default.vsh", "shaders/sc_default.fsh");
        JsonValue model = Assets.<JsonValue>get("meshes/raider_pnt.mesh");
        meshes = new HashMap<>();
        meshes.put("scout", new MeshBuilder().load(model, new float[] { 0.25f, 0.0f, 0.3125f, 0.0625f }));
        meshes.put("raider", new MeshBuilder().load(model, new float[] { 0.25f, 0.0625f, 0.3125f, 0.125f }));
        meshes.put("outpost", new MeshBuilder().load(model, new float[] { 0.3125f, 0.0f, 0.375f, 0.0625f }));
        meshes.put("colony_ship", new MeshBuilder().load(model, new float[] { 0.3125f, 0.0625f, 0.375f, 0.125f }));
        // uses raider mesh
        meshes.put("pirate", new MeshBuilder().load(model, new float[] { 0.25f, 0.0625f, 0.3125f, 0.125f }));
        bar = new MeshBuilder().buildSquare();
    }
    
    public void render(Camera cam, List<Ship> ships) {
        Matrix4 mvp = new Matrix4();
        Matrix4 model = new Matrix4();

        // TODO: optimize as part of render queue refwrite
        List<Ship> visible = new ArrayList<>();
        
        sp.begin();
        for (Ship ship : ships) {
            Vector3 pos = ship.getCoordinates().toWorld();
            pos.add(OFFSET);
            if (!cam.frustum.sphereInFrustum(pos, SIZE)) {
                continue;
            }            
            visible.add(ship);
            
            model.setToTranslationAndScaling(pos.x, pos.y, pos.z, SIZE, SIZE, SIZE);
            mvp.set(cam.combined);
            mvp.mul(model);

            sp.setUniformMatrix("u_mvp", mvp);
            sp.setUniformf("u_color", ship.getOwner().getColor());

            Mesh mesh = meshes.get(ship.getType());
            mesh.render(sp, GL20.GL_TRIANGLES);
        }
        sp.end();
        
        // TODO: move to dedicated overlay rendering phase
        defaultSp.begin();
        for (Ship ship : visible) {
            float shield = ship.getShieldPercentage();
            Color shieldColor = new Color(1.0f, 1.0f, 1.0f, shield);
            Vector3 world = ship.getCoordinates().toWorld();
            model.setToTranslationAndScaling(world.x, 0.0f, world.z + 0.5f, 0.3f, 0.15f, 0.075f);
            mvp.set(cam.combined);
            mvp.mul(model);
            defaultSp.setUniformMatrix("u_mvp", mvp);
            defaultSp.setUniformf("u_color", shieldColor);
            bar.render(defaultSp, GL20.GL_TRIANGLES);

            float crew = ship.getCrewPercentage();
            Color crewColor = new Color(1.0f - crew, crew, 0.0f, 1.0f);
            model.setToTranslationAndScaling(world.x, 0.0f, world.z + 0.65f, 0.3f, 0.15f, 0.075f);
            mvp.set(cam.combined);
            mvp.mul(model);
            defaultSp.setUniformMatrix("u_mvp", mvp);
            defaultSp.setUniformf("u_color", crewColor);
            bar.render(defaultSp, GL20.GL_TRIANGLES);
        }
        defaultSp.end();
    }
    
    @Override
    public void dispose() {
        for (Mesh m : meshes.values()) {
            m.dispose();
        }
        bar.dispose();
        sp.dispose();
    }
}