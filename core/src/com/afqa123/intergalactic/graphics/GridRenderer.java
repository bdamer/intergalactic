package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.math.Hex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class GridRenderer implements Disposable {
    
    // Number of vertices per hex
    private static final int NUM_VERTICES = 6;
    // Number of indices per hex
    private static final int NUM_INDICES = 12;
    // Number of elements per vertex
    private static final int VERTEX_SIZE = 7;
    // Padding between grid cells
    private static final float PADDING = 0.05f;
    
    private final ShaderProgram sp;
    private final Galaxy galaxy;    
    private final Mesh mesh;
    
    public GridRenderer(Galaxy galaxy) {
        this.galaxy = galaxy;
        this.sp = ShaderFactory.buildShader("shaders/sc_color.vsh", "shaders/sc_color.fsh");
        this.mesh = new Mesh(true, galaxy.getCount() * NUM_VERTICES, 
            galaxy.getCount() * NUM_INDICES, 
            new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
    }
    
    /**
     * Rebuilds the grid mesh.
     */
    public void update() {
        // TODO: use lists (although ideally, we'd only want to update the
        // vertices that have changed)
        float[] vertices = new float[galaxy.getCount() * NUM_VERTICES * VERTEX_SIZE];
        short[] indices = new short[galaxy.getCount() * NUM_INDICES];
        
        int counter = 0;
        Sector[][] sectors = galaxy.getSectors();
        for (Sector[] row : sectors) {
            for (Sector s : row) {
                if (!s.isVisible())
                    continue;                
                addHex(s, vertices, indices, counter);
                counter++;
            }
        }
        
        // Update mesh
        mesh.setVertices(vertices, 0, counter * NUM_VERTICES * VERTEX_SIZE);
        mesh.setIndices(indices, 0, counter * NUM_INDICES);
        
        Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh vertices: %d", mesh.getNumVertices()));
        Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh indices: %d", mesh.getNumIndices()));
    }
    
    private void addHex(Sector s, float[] vertices, short[] indices, int vCount) {
        //    1
        // 6     2
        //    X
        // 5     3
        //    4

        // Compute center coordinates of hex
        Vector3 pos = s.getCoordinates().toWorld();
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        float a = 0.5f;
        
        int vIndex = vCount * NUM_VERTICES * VERTEX_SIZE;
        
        // Vertex 1        
        vertices[vIndex++] = pos.x;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.SIZE + PADDING;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        // Vertex 2
        vertices[vIndex++] = pos.x + Hex.HEIGHT - PADDING;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.HALF_SIZE;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        // Vertex 3
        vertices[vIndex++] = pos.x + Hex.HEIGHT - PADDING;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.HALF_SIZE;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        // Vertex 4
        vertices[vIndex++] = pos.x;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.SIZE - PADDING;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        // Vertex 5
        vertices[vIndex++] = pos.x - Hex.HEIGHT + PADDING;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.HALF_SIZE;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        // Vertex 6
        vertices[vIndex++] = pos.x - Hex.HEIGHT + PADDING;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.HALF_SIZE;
        vertices[vIndex++] = r;
        vertices[vIndex++] = g;
        vertices[vIndex++] = b;
        vertices[vIndex++] = a;
        
        int iIndex = vCount * NUM_INDICES;
        indices[iIndex++] = (short)(vCount * NUM_VERTICES);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 1);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 1);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 2);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 2);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 3);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 3);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 4);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 4);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 5);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 5);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES);
    }
        
    public void render(Camera cam) {
        sp.begin();
        sp.setUniformMatrix("u_mvp", cam.combined);
        mesh.render(sp, GL20.GL_LINES);
        sp.end();
    }

    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
    }
}