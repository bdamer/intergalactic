package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class CubeRenderer implements Disposable {

    // Position + Normal
    private static final int VERTEX_SIZE = 6;
    private final ShaderProgram sp;
    private final Light light;
    private final Vector3 material;
    private Mesh mesh;
    private Mesh normalMesh;

    public CubeRenderer() {
        this.sp = ShaderFactory.buildShader("shaders/star.vsh", "shaders/star.fsh");
        this.light = new DirectionalLight(new Vector3(0.0f, -1.0f, -0.5f), new Vector3(1.0f, 1.0f, 1.0f), 0.05f);
        this.material = new Vector3(1.0f, 0.0f, 1.0f);
        buildMesh();
    }
    
    private void buildMesh() {
        final float[] vertices = new float[] {
            // bottom face
            -1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f,
             1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f,
            -1.0f, -1.0f,  1.0f, 0.0f, -1.0f, 0.0f,
             1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f,
             1.0f, -1.0f,  1.0f, 0.0f, -1.0f, 0.0f,
            -1.0f, -1.0f,  1.0f, 0.0f, -1.0f, 0.0f,
            // top face
            -1.0f, 1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
             1.0f, 1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
             1.0f, 1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
             1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            // back face
             1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
             1.0f,  1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
            -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
             1.0f,  1.0f, -1.0f, 0.0f, 0.0f, -1.0f,
            // front face
            -1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
             1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -1.0f,  1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
             1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
             1.0f,  1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -1.0f,  1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            // left face
            -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
            -1.0f, -1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
            -1.0f, -1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
            -1.0f,  1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
            // right face
            1.0f, -1.0f,  1.0f, 1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
            1.0f,  1.0f,  1.0f, 1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
            1.0f,  1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
            1.0f,  1.0f,  1.0f, 1.0f, 0.0f, 0.0f
        };

        mesh = new Mesh(true, vertices.length / VERTEX_SIZE, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        mesh.setVertices(vertices);
        
        normalMesh = DebugTools.createNormalMesh(vertices);
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
        
        DebugTools.renderNormalMesh(mvp, normalMesh);
     }

    @Override
    public void dispose() {
        mesh.dispose();
        normalMesh.dispose();
        sp.dispose();
    }
}
