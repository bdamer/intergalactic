package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.math.Geometry;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {

    // TODO: make builder more flexible by allowing selection of vertex type
    private enum VertexType {

        POS3_NOR3(6),
        POS3_COL4(7),
        POS3_UV2(5),
        POS3_NOR3_UV2(8);
        
        private final int size;
        
        private VertexType(int size) {
            this.size = size;
        }
        
        public int size() {
            return size;
        }        
    };
    
    /**
     * Builds up a sphere mesh of {@code POS3_NOR3} vertices with a given number
     * of slices and stacks.
     * 
     * @param slices The number of slices.
     * @param stacks The number of stacks.
     * @return The mesh.
     */
    public Mesh buildSphere(int slices, int stacks) {
        List<Float> vertexData = new ArrayList<>();
        List<Float> normalData = new ArrayList<>();

        int numVertices = Geometry.generateSphere(1.0f, slices, stacks, vertexData, normalData);
        int numIndices = (slices + 1) * 2 * stacks;                        
        Mesh mesh = new Mesh(true, numVertices, numIndices, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        
        // Build up vertex array
        float[] vertices = new float[numVertices * VertexType.POS3_NOR3.size()];
        for (int i = 0; i < numVertices; i++) {
            int offset = i * VertexType.POS3_NOR3.size();
            vertices[offset++] = vertexData.get(i * 3 + 0);
            vertices[offset++] = vertexData.get(i * 3 + 1);
            vertices[offset++] = vertexData.get(i * 3 + 2);
            vertices[offset++] = normalData.get(i * 3 + 0);
            vertices[offset++] = normalData.get(i * 3 + 1);
            vertices[offset++] = normalData.get(i * 3 + 2);            
        }
        mesh.setVertices(vertices);        

        // First, generate vertex index arrays for drawing with glDrawElements
        // All stacks, including top and bottom are covered with a triangle
        // strip.
        short[] indices = new short[numIndices];        
        
        // top stack
        int idx = 0;
        for (int j = 0; j < slices; j++, idx += 2) {
            indices[idx] = (short)(j + 1); // 0 is top vertex, 1 is first for first stack
            indices[idx + 1] = 0;
        }
        indices[idx] = (short)1; // repeat first slice's idx for closing off shape
        indices[idx+1] = 0;
        idx += 2;

        // middle stacks:
        // Strip indices are relative to first index belonging to strip, NOT relative to first vertex/normal pair in array
        int offset;
        for (int i = 0; i < stacks - 2; i++, idx += 2) {
            offset = 1 + i * slices;                    // triangle_strip indices start at 1 (0 is top vertex), and we advance one stack down as we go along
            for (int j = 0; j < slices; j++, idx += 2) {
                indices[idx] = (short)(offset + j + slices);
                indices[idx+1] = (short)(offset + j);
            }
            indices[idx] = (short)(offset + slices);      // repeat first slice's idx for closing off shape
            indices[idx+1] = (short)offset;
        }

        // bottom stack
        offset = 1 + (stacks - 2) * slices;  // triangle_strip indices start at 1 (0 is top vertex), and we advance one stack down as we go along
        for (int j = 0; j < slices; j++, idx += 2)
        {
            indices[idx] = (short)(numVertices - 1); // zero based index, last element in array (bottom vertex)...
            indices[idx+1] = (short)(offset+j);
        }
        indices[idx] = (short)(numVertices - 1);   // repeat first slice's idx for closing off shape
        indices[idx+1] = (short)offset;

        mesh.setIndices(indices);
        return mesh;
    }
    
    /**
     * Builds up a spiral mesh of {@code POS3_COL4} vertices.
     * 
     * @param numSegments Number of spiral segments
     * @param step Step between each segments in radians.
     * @param radius Max radius of the spiral.
     * @param color The color.
     * @return The mesh.
     */
    public Mesh buildSpiral(int numSegments, float step, float radius, Color color) {        
        float[] vertices = new float[numSegments * VertexType.POS3_COL4.size()];
        
        Mesh mesh = new Mesh(true, numSegments, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

        float angle = 0.0f;
        for (int idx = 0; idx < numSegments; idx++) {
            int offset = idx * VertexType.POS3_COL4.size();
            vertices[offset++] = (float)Math.cos(angle) * (float)idx / (float)numSegments * radius;
            vertices[offset++] = (float)Math.sin(angle) * (float)idx / (float)numSegments * radius;
            vertices[offset++] = 0.0f;
            vertices[offset++] = color.r;
            vertices[offset++] = color.g;
            vertices[offset++] = color.b;            
            vertices[offset++] = color.a;            
            angle += step;
        }        
        mesh.setVertices(vertices);
        
        return mesh;
    }
    
    /**
     * Builds up a cube mesh of {@code POS3_NOR3} vertices.
     * 
     * @return The mesh.
     */
    public Mesh buildCube() {
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

        Mesh mesh = new Mesh(true, vertices.length / VertexType.POS3_NOR3.size(), 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        mesh.setVertices(vertices);
        
        return mesh;
    }

    /**
     * Builds up a square mesh of {@code POS3_COL4} vertices.
     * 
     * @param Color The color.
     * @return The mesh.
     */
    public Mesh buildSquare(Color color) {
        Mesh mesh = new Mesh(true, 6, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

        float[] vertices = new float[] { 
            -1.0f, 1.0f,  1.0f, color.r, color.g, color.b, color.a,
             1.0f, 1.0f,  1.0f, color.r, color.g, color.b, color.a,
            -1.0f, 1.0f, -1.0f, color.r, color.g, color.b, color.a,
             1.0f, 1.0f,  1.0f, color.r, color.g, color.b, color.a,
             1.0f, 1.0f, -1.0f, color.r, color.g, color.b, color.a,
            -1.0f, 1.0f, -1.0f, color.r, color.g, color.b, color.a
        };        
        mesh.setVertices(vertices);
        
        return mesh;
    }
}