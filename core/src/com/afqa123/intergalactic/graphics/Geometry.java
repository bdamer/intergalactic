package com.afqa123.intergalactic.graphics;

import java.util.List;

public class Geometry {

    /**
     * Generates vertices and normals for a sphere based on fghGenerateSphere
     * from freeglut.
     * 
     * @param radius The radius of the sphere.
     * @param slices The number of slices.
     * @param stacks The number of stacks.
     * @param vertices A list that will contain the vertex components.
     * @param normals A list that will contain the normal components.
     * @return The number of vertices generated.
     */
    public static int generateSphere(float radius, int slices, int stacks, List<Float> vertices, 
            List<Float> normals) {
        // number of unique vertices
        if (slices == 0 || stacks < 2) {
            // nothing to generate
            return 0 ;
        }
        int nVert = slices * (stacks - 1) + 2;
        if (nVert > 65535) {
            // limit of glushort, thats 256*256 subdivisions, should be enough in practice. See note above
            throw new RuntimeException("Too many slices or stacks requested.");
        }

        // precompute values on unit circle
        float[] sint1 = new float[slices + 1];
        float[] cost1 = new float[slices + 1];
        float[] sint2 = new float[stacks + 1];
        float[] cost2 = new float[stacks + 1];
        circleTable(sint1, cost1, -slices, false);
        circleTable(sint2, cost2, stacks, true);

        // top
        vertices.add(0.0f);
        vertices.add(radius);
        vertices.add(0.0f);
        normals.add(0.0f);
        normals.add(1.0f);
        normals.add(0.0f);
        
        // each stack
        float x, y, z;
        for (int i = 1; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                x = cost1[j] * sint2[i];
                y = cost2[i];
                z = -sint1[j] * sint2[i];
                vertices.add(x * radius);
                vertices.add(y * radius);
                vertices.add(z * radius);
                normals.add(x);
                normals.add(y);
                normals.add(z);                
            }
        }

        // bottom
        vertices.add(0.0f);
        vertices.add(-radius);
        vertices.add(0.0f);
        normals.add(0.0f);
        normals.add(-1.0f);
        normals.add(0.0f);
        
        return nVert;
    }    
    
    /**
     * Compute lookup table of cos and sin values forming a circle
     * (or half circle if halfCircle == true). Based on fghCircleTable from
     * freeglut.
     * 
     * Notes:
     *    The size of the table is (n+1) to form a connected loop
     *    The last entry is exactly the same as the first
     *    The sign of n can be flipped to get the reverse loop
     * 
     * @param sint The sin table.
     * @param cost The cos table.
     * @param n The number of entries in each table.
     * @param halfCircle If true will compute the values forming a half-circle.
     */    
    private static void circleTable(float[] sint, float[] cost, final int n, final boolean halfCircle)
    {
        // Table size, the sign of n flips the circle direction
        final int size = Math.abs(n);

        // Determine the angle between samples
        final float angle = (halfCircle ? 1 : 2) * (float)Math.PI / (float)( ( n == 0 ) ? 1 : n );

        // Compute cos and sin around the circle
        sint[0] = 0.0f;
        cost[0] = 1.0f;

        for (int i = 1; i < size; i++) {
            sint[i] = (float)Math.sin(angle * i);
            cost[i] = (float)Math.cos(angle * i);
        }

        if (halfCircle) {
            sint[size] = 0.0f;  // sin PI
            cost[size] = -1.0f;  // cos PI
        } else {
            // Last sample is duplicate of the first (sin or cos of 2 PI)
            sint[size] = sint[0];
            cost[size] = cost[0];
        }
    }
}
