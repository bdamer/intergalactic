package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class BackgroundRenderer {

    private final ShaderProgram sp;
    private final Mesh mesh;
    private final Texture texture;
    private final Matrix4 modelM;
    
    public BackgroundRenderer() {
        sp = ShaderFactory.buildShader("shaders/textured.vsh", "shaders/textured.fsh");
        mesh = new Mesh(true, 4, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
        texture = Assets.get("textures/nebula32.png");
        modelM = new Matrix4();
        modelM.setToTranslationAndScaling(0.0f, -2.0f, 0.0f, 25.0f, 1.0f, 25.0f);
        buildMesh();
    }
    
    private void buildMesh() {
        /**
         * 2 3
         * 0 1
         */
        float[] vertices = new float[] {
            -1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, -1.0f, 1.0f, 0.0f            
        };
        mesh.setVertices(vertices);
    }
    
    public void render(Camera cam) {
        Matrix4 mvp = new Matrix4(modelM);
        mvp.mulLeft(cam.combined);

        texture.bind(0);
        
        sp.begin();
        sp.setUniformMatrix("u_worldView", mvp);
        sp.setUniformi("u_tex0", 0);
        mesh.render(sp, GL20.GL_TRIANGLE_STRIP);
        sp.end();        
    }

    public void dispose() {
        mesh.dispose();
        sp.dispose();
        texture.dispose();
    }    
}