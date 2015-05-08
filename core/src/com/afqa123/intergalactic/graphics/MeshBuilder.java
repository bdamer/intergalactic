package com.afqa123.intergalactic.graphics;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {

    private static final int VERTEX_SIZE = 6;
    
    public Mesh buildSphere(int slices, int stacks) {
        List<Float> vertexData = new ArrayList<>();
        List<Float> normalData = new ArrayList<>();

        int numVertices = Geometry.generateSphere(1.0f, slices, stacks, vertexData, normalData);
        int numIndices = (slices + 1) * 2 * stacks;                        
        Mesh mesh = new Mesh(true, numVertices, numIndices, 
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
}