package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class CubeRenderer implements Disposable {

    private final ShaderProgram sp;
    private final Light light;
    private final Vector3 material;
    private Mesh mesh;

    public CubeRenderer() {
        this.sp = ShaderFactory.buildShader("shaders/sc_star.vsh", "shaders/sc_star.fsh");
        this.light = new DirectionalLight(new Vector3(0.0f, -1.0f, -0.5f), new Vector3(1.0f, 1.0f, 1.0f), 0.05f);
        this.material = new Vector3(1.0f, 0.0f, 1.0f);
        this.mesh = new MeshBuilder().buildCube();
    }
    
    public void render(Camera cam) {        
        Matrix4 model = new Matrix4();

        Matrix4 mvp = new Matrix4();
        mvp.set(model);
        mvp.mul(cam.combined);        
                
        sp.begin();
        sp.setUniformMatrix("u_model", model);
        sp.setUniformMatrix("u_mvp", mvp);
        sp.setUniformf("u_diffuse", material);
        light.bind(sp);
        mesh.render(sp, GL20.GL_TRIANGLES);
        sp.end();
     }

    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
    }
}
