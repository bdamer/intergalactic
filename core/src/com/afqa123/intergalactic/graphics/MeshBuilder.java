package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.math.Geometry;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.List;

// TODO: make builder more flexible by allowing selection of vertex type
public class MeshBuilder {

    // TODO: move
    public static final VertexAttribute ATTR_POS3 = new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE);
    public static final VertexAttribute ATTR_NOR3 = new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE);
    public static final VertexAttribute ATTR_COL4 = new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE);
    public static final VertexAttribute ATTR_UV2 = new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE);
    
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
        float[] vertices = new float[numVertices * VertexFormat.POS3_NOR3.size()];
        for (int i = 0; i < numVertices; i++) {
            int offset = i * VertexFormat.POS3_NOR3.size();
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
        float[] vertices = new float[numSegments * VertexFormat.POS3_COL4.size()];
        
        Mesh mesh = new Mesh(true, numSegments, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

        float angle = 0.0f;
        for (int idx = 0; idx < numSegments; idx++) {
            int offset = idx * VertexFormat.POS3_COL4.size();
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
        return load(Assets.<JsonValue>get("meshes/cube_pn.mesh"));
    }

    /**
     * Builds up a square mesh of {@code POS3} vertices.
     * 
     * @return The mesh.
     */
    public Mesh buildSquare() {
        Mesh mesh = new Mesh(true, 6, 0, 
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        float[] vertices = new float[] { 
            -1.0f, 1.0f,  1.0f,
             1.0f, 1.0f,  1.0f,
            -1.0f, 1.0f, -1.0f,
             1.0f, 1.0f,  1.0f,
             1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f
        };        
        mesh.setVertices(vertices);        
        return mesh;
    }
    
    /**
     * Loads a {@code Mesh} from a JSON value.
     * 
     * @param val The JSON value.
     * @return The mesh.
     */
    public Mesh load(JsonValue val) {
        return load(val, null);
    }
    
    /**
     * Loads a {@code Mesh} from a JSON value. Any UV coordinates will be transformed
     * using the provided offset.
     * 
     * @param val The JSON value.
     * @param textureOffset {left, top, right, bottom} transformation.
     * @return The mesh.
     */
    public Mesh load(JsonValue val, float[] textureOffset) {
        Json json = new Json();
        VertexFormat fmt = json.readValue("format", VertexFormat.class, val);
        float[] vertices = json.readValue("vertices", float[].class, val);
        short[] indices = json.readValue("indices", short[].class, val);
        Mesh mesh = new Mesh(true, vertices.length / fmt.size(), indices.length, fmt.getAttributes());

        if (textureOffset != null) {
            for (int i = 0; i < vertices.length; i += fmt.size()) {
                switch (fmt) {
                    case POS3_UV2:
                        vertices[i + 3] = textureOffset[0] + vertices[i + 3] * (textureOffset[2] - textureOffset[0]);
                        vertices[i + 4] = textureOffset[1] + vertices[i + 4] * (textureOffset[3] - textureOffset[1]);
                        break;
                    case POS3_NOR3_UV2:
                        vertices[i + 6] = textureOffset[0] + vertices[i + 6] * (textureOffset[2] - textureOffset[0]);
                        vertices[i + 7] = textureOffset[1] + vertices[i + 7] * (textureOffset[3] - textureOffset[1]);
                        break;
                    default:
                        // no texture coords
                        break;
                }
            }
        }
        
        mesh.setVertices(vertices);
        mesh.setIndices(indices);
        return mesh;
    }
}