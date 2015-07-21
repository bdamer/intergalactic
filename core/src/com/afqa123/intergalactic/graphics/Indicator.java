package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.math.Hex;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Indicator implements Disposable {

    private final static Vector3 SCALE_VECTOR = new Vector3(Hex.HEIGHT, 1.0f, Hex.HEIGHT);
    private final ShaderProgram sp;
    private final Texture texture;
    private final Mesh mesh;
    private final Matrix4 modelM;
    private Vector3 position;
    
    public Indicator() {
        sp = ShaderFactory.buildShader("shaders/textured.vsh", "shaders/transparency.fsh");
        texture = Assets.get("textures/catalog01.png");
        mesh = new Mesh(true, 4, 0,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
        buildMesh();
        modelM = new Matrix4();
    }

    // TODO: reuse
    private void buildMesh() {
        /**
         * 2 3
         * 0 1
         */
        float[] vertices = new float[] {
            -1.0f, 0.0f, 1.0f, 0.0f, 0.125f,
            1.0f, 0.0f, 1.0f, 0.125f, 0.125f,
            -1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, -1.0f, 0.125f, 0.0f            
        };
        mesh.setVertices(vertices);
    }
    
    public void render(Camera cam) {
        if (position == null) {
            return;
        }
        Matrix4 mvp = new Matrix4(modelM);
        modelM.setToTranslationAndScaling(position, SCALE_VECTOR);
        mvp.mulLeft(cam.combined);

        texture.bind(0);
        
        sp.begin();
        sp.setUniformMatrix("u_worldView", mvp);
        sp.setUniformi("u_tex0", 0);
        mesh.render(sp, GL20.GL_TRIANGLE_STRIP);
        sp.end();   
    }
    
    @Override
    public void dispose() {
        texture.dispose();
        mesh.dispose();
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }    
}