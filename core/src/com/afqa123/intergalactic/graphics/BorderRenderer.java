package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.math.Edge;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;

public class BorderRenderer implements Disposable {

    private static final int VERTEX_SIZE = 3;
    private static final float BORDER_WIDTH = 5.0f;
    private final ShaderProgram sp;
    private final Color color;
    private Mesh mesh;
    
    public BorderRenderer(Color color) {
        this.sp = ShaderFactory.buildShader("shaders/sc_default.vsh", "shaders/sc_default.fsh");
        this.color = color;
    }
    
    /**
     * Rebuilds the grid mesh.
     */
    public void update(List<Edge> edges) {
        if (mesh != null) {
            mesh.dispose();
        }
        
        float[] vertices = new float[edges.size() * 2 * VERTEX_SIZE];

        mesh = new Mesh(true, edges.size() * 2, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        int i = 0;
        for (Edge edge : edges) {
            vertices[i++] = edge.getFrom().x;
            vertices[i++] = 0.0f;
            vertices[i++] = edge.getFrom().z;
            
            vertices[i++] = edge.getTo().x;
            vertices[i++] = 0.0f;
            vertices[i++] = edge.getTo().z;
        }        
        mesh.setVertices(vertices, 0, edges.size() * 2 * VERTEX_SIZE);        
    }
    
    public void render(Camera cam) { 
        Gdx.gl.glLineWidth(BORDER_WIDTH);
        sp.begin();
        sp.setUniformMatrix("u_mvp", cam.combined);
        sp.setUniformf("u_color", color);
        mesh.render(sp, GL20.GL_LINES);
        sp.end();
    }
    
    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
    }
}