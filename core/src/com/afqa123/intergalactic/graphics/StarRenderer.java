package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Sector;
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
import java.util.ArrayList;
import java.util.List;

public class StarRenderer implements Disposable {

    private static final int VERTEX_SIZE = 6;
    private static final int SLICES = 32;
    private static final int STACKS = 24;
    private final ShaderProgram sp;
    private final Light light;
    private final Vector3 material;
    private final Matrix4 modelM;
    private final float turbulence;
    private final float gradient;
    private Mesh mesh;
    // seed value to animate surface
    private final long start;
        
    private final Texture explosion;
    
    public StarRenderer(Sector sector) {
        this.sp = ShaderFactory.buildShader("shaders/sc_star_noise.vsh", "shaders/sc_star_noise.fsh");
        this.light = new DirectionalLight(new Vector3(0.0f, -1.0f, -0.5f), 
                new Vector3(1.0f, 1.0f, 1.0f), 0.05f);
        
        start = System.currentTimeMillis() - (long)(Math.random() * 1000000.0);
        float scale;        
        switch (sector.getCategory()) {
            case BLUE:
                scale = 0.4f;
                turbulence = 1.0f / 30000.0f;
                material = new Vector3(0.0f, 0.0f, 1.0f);
                gradient = 20.0f / 32.0f;
                break;
            case WHITE:
                scale = 0.3f;
                turbulence = 1.0f / 30000.0f;
                material = new Vector3(1.0f, 1.0f, 1.0f);
                gradient = 15.0f / 32.0f;
                break;
            case YELLOW:
                scale = 0.25f;
                turbulence = 1.0f / 35000.0f;
                material = new Vector3(1.0f, 1.0f, 0.0f);
                gradient = 10.0f / 32.0f;
                break;
            case ORANGE:
                scale = 0.2f;
                turbulence = 1.0f / 30000.0f;
                material = new Vector3(1.0f, 0.35f, 0.0f);
                gradient = 5.0f / 32.0f;
                break;
            case RED:
                scale = 0.125f;
                turbulence = 1.0f / 30000.0f;
                material = new Vector3(1.0f, 0.0f, 0.0f);
                gradient = 0.0f / 32.0f;
                break;                
            default:
                throw new RuntimeException("Unsupported sector type: " + sector.getCategory());
        }
        
        this.modelM = new Matrix4();
        Vector3 pos = sector.getCoordinates().toWorld();
        this.modelM.setToTranslationAndScaling(pos, new Vector3(scale, scale, scale));
                
        buildMesh();
        
        explosion = Assets.get("textures/explosion.png");
    }
    
    private void buildMesh() {
        List<Float> vertexData = new ArrayList<>();
        List<Float> normalData = new ArrayList<>();

        int numVertices = Geometry.generateSphere(1.0f, SLICES, STACKS, vertexData, normalData);
        int numIndices = (SLICES + 1) * 2 * STACKS;                        
        mesh = new Mesh(true, numVertices, numIndices, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        
        // Build up vertex array
        float[] vertices = new float[numVertices * VERTEX_SIZE];
        for (int i = 0; i < numVertices; i++) {
            vertices[i * VERTEX_SIZE + 0] = vertexData.get(i * 3 + 0);
            vertices[i * VERTEX_SIZE + 1] = vertexData.get(i * 3 + 1);
            vertices[i * VERTEX_SIZE + 2] = vertexData.get(i * 3 + 2);
            vertices[i * VERTEX_SIZE + 3] = normalData.get(i * 3 + 0);
            vertices[i * VERTEX_SIZE + 4] = normalData.get(i * 3 + 1);
            vertices[i * VERTEX_SIZE + 5] = normalData.get(i * 3 + 2);            
        }
        mesh.setVertices(vertices);        

        // First, generate vertex index arrays for drawing with glDrawElements
        // All stacks, including top and bottom are covered with a triangle
        // strip.
        short[] indices = new short[numIndices];        
        
        // top stack
        int idx = 0;
        for (int j = 0; j < SLICES; j++, idx += 2) {
            indices[idx] = (short)(j + 1); // 0 is top vertex, 1 is first for first stack
            indices[idx + 1] = 0;
        }
        indices[idx] = (short)1; // repeat first slice's idx for closing off shape
        indices[idx+1] = 0;
        idx += 2;

        // middle stacks:
        // Strip indices are relative to first index belonging to strip, NOT relative to first vertex/normal pair in array
        int offset;
        for (int i = 0; i < STACKS - 2; i++, idx += 2) {
            offset = 1 + i * SLICES;                    // triangle_strip indices start at 1 (0 is top vertex), and we advance one stack down as we go along
            for (int j = 0; j < SLICES; j++, idx += 2) {
                indices[idx] = (short)(offset + j + SLICES);
                indices[idx+1] = (short)(offset + j);
            }
            indices[idx] = (short)(offset + SLICES);      // repeat first slice's idx for closing off shape
            indices[idx+1] = (short)offset;
        }

        // bottom stack
        offset = 1 + (STACKS - 2) * SLICES;  // triangle_strip indices start at 1 (0 is top vertex), and we advance one stack down as we go along
        for (int j = 0; j < SLICES; j++, idx += 2)
        {
            indices[idx] = (short)(numVertices - 1); // zero based index, last element in array (bottom vertex)...
            indices[idx+1] = (short)(offset+j);
        }
        indices[idx] = (short)(numVertices - 1);   // repeat first slice's idx for closing off shape
        indices[idx+1] = (short)offset;

        mesh.setIndices(indices);
    }
 
    public void render(Camera cam) {
        Matrix4 mvp = new Matrix4();
        mvp.set(cam.combined);
        mvp.mul(modelM);
        
        explosion.bind(0);
        sp.begin();
        sp.setUniformMatrix("u_mvp", mvp);
        sp.setUniformi("u_tex0", 0);
//        sp.setUniformMatrix("u_model", modelM);
//        sp.setUniformf("u_diffuse", material);
//        light.bind(sp);
        sp.setUniformf("u_time", turbulence * (System.currentTimeMillis() - start) );
        sp.setUniformf("u_gradient", gradient);                
        mesh.render(sp, GL20.GL_TRIANGLE_STRIP);
        sp.end();
    }

    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
    }
}