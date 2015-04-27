package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class DebugTools {

    private static final int VERTEX_SIZE = 6;
    
    /**
     * Creates new mesh containing vertices for the normal vectors of a given
     * set of vertices.
     * 
     * @param vertices Assumed to be 3 position components (x,y,z) followed by
     * 3 normal components (nx, ny, nz).
     * @return A mesh.
     */
    public static Mesh createNormalMesh(float[] vertices) {
        final float[] normalVertices = new float[vertices.length];        
        for (int i = 0; i < vertices.length; i += VERTEX_SIZE) {
            normalVertices[i + 0] = vertices[i + 0];
            normalVertices[i + 1] = vertices[i + 1];
            normalVertices[i + 2] = vertices[i + 2];
            normalVertices[i + 3] = vertices[i + 0] + vertices[i + 3];
            normalVertices[i + 4] = vertices[i + 1] + vertices[i + 4];
            normalVertices[i + 5] = vertices[i + 2] + vertices[i + 5];    
        }        
        Mesh mesh = new Mesh(true, vertices.length / 3, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        mesh.setVertices(normalVertices);
        return mesh;        
    }
    
    // TODO: implement caching in shader mgr...
    private static ShaderProgram sp;
    
    public static void renderNormalMesh(Matrix4 mvp, Mesh normalMesh) {
        if (sp == null) {
            sp = ShaderFactory.buildShader("shaders/default.vsh", "shaders/default.fsh");            
        }        
        Vector3 color = new Vector3(1.0f, 0.0f, 0.0f);
        sp.begin();
        sp.setUniformMatrix("u_mvp", mvp);
        sp.setUniformf("u_color", color);
        normalMesh.render(sp, GL20.GL_LINES);
        sp.end();        
    }
    
}
