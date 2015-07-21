package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.FactionMap;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.math.Hex;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.SectorStatus;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class GridRenderer implements Disposable {

    private static final Vector2 TEXTURE_OFFSET = new Vector2(0.0f, 0.25f);
    private static final Vector2 TEXTURE_SIZE = new Vector2(0.25f, 0.25f);
    // Number of vertices per hex
    private static final int NUM_VERTICES = 4;
    // Number of indices per hex
    private static final int NUM_INDICES = 6;
    // Number of elements per vertex
    private static final int VERTEX_SIZE = 5;
    private static final Color GRID_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.6f);
    private static final Map<Range, Color> RANGE_COLORS;
    
    static {
        RANGE_COLORS = new HashMap<>();
        RANGE_COLORS.put(Range.SHORT, new Color(0.0f, 1.0f, 0.0f, 0.5f));
        RANGE_COLORS.put(Range.LONG, new Color(1.0f, 1.55f, 0.0f, 0.6f));
    }
    
    private final ShaderProgram sp;
    private final Galaxy galaxy;    
    private final Mesh mesh;
    private final Texture hexTexture;
    
    public GridRenderer(Galaxy galaxy) {
        this.galaxy = galaxy;
        this.sp = ShaderFactory.buildShader("shaders/sc_hexgrid.vsh", "shaders/sc_hexgrid.fsh");
        this.hexTexture = Assets.get("textures/catalog01.png");
        this.mesh = new Mesh(true, galaxy.getCount() * NUM_VERTICES, 
            galaxy.getCount() * NUM_INDICES, 
            new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
    }
    
    /**
     * Rebuilds the grid mesh.
     */
    public void update(FactionMap map) {
        // TODO: use lists (although ideally, we'd only want to update the
        // vertices that have changed)
        float[] vertices = new float[galaxy.getCount() * NUM_VERTICES * VERTEX_SIZE];
        short[] indices = new short[galaxy.getCount() * NUM_INDICES];
        
        int counter = 0;
        Sector[][] sectors = galaxy.getSectors();
        FactionMapSector[][] entries = map.getSectors();
        for (int row = 0; row < sectors.length; row++) {
            for (int col = 0; col < sectors[row].length; col++) {
                // TODO: use player faction map to determine any additional properties
                Sector sector = sectors[row][col];
                FactionMapSector factionSector = entries[row][col];
                if (factionSector.getStatus() == SectorStatus.UNKNOWN) {
                    continue;
                }
                addSector(sector.getCoordinates().toWorld(), vertices, indices, counter);
                counter++;
            }
        }
        
        // Update mesh
        mesh.setVertices(vertices, 0, counter * NUM_VERTICES * VERTEX_SIZE);
        mesh.setIndices(indices, 0, counter * NUM_INDICES);
        
        //Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh vertices: %d", mesh.getNumVertices()));
        //Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh indices: %d", mesh.getNumIndices()));
    }
    
    private void addSector(Vector3 pos, float[] vertices, short[] indices, int vCount) {
        int vIndex = vCount * NUM_VERTICES * VERTEX_SIZE;
        // Vertex order:
        // 2 3
        // 0 1

        // Vertex 0
        vertices[vIndex++] = pos.x - Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.SIZE;
        vertices[vIndex++] = TEXTURE_OFFSET.x;
        vertices[vIndex++] = TEXTURE_OFFSET.y + TEXTURE_SIZE.y;
        
        // Vertex 1
        vertices[vIndex++] = pos.x + Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.SIZE;
        vertices[vIndex++] = TEXTURE_OFFSET.x + TEXTURE_SIZE.x;
        vertices[vIndex++] = TEXTURE_OFFSET.y + TEXTURE_SIZE.y;

        // Vertex 2
        vertices[vIndex++] = pos.x - Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.SIZE;
        vertices[vIndex++] = TEXTURE_OFFSET.x;
        vertices[vIndex++] = TEXTURE_OFFSET.y;

        // Vertex 3
        vertices[vIndex++] = pos.x + Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.SIZE;
        vertices[vIndex++] = TEXTURE_OFFSET.x + TEXTURE_SIZE.x;
        vertices[vIndex++] = TEXTURE_OFFSET.y;
    
        int iIndex = vCount * NUM_INDICES;
        indices[iIndex++] = (short)(vCount * NUM_VERTICES);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 1);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 2);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 2);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 1);
        indices[iIndex++] = (short)(vCount * NUM_VERTICES + 3);
    }
        
    public void render(Camera cam) { 
        hexTexture.bind(0);        
        sp.begin();
        sp.setUniformMatrix("u_mvp", cam.combined);
        sp.setUniformi("u_tex0", 0);
        sp.setUniformf("u_color", GRID_COLOR);
        mesh.render(sp, GL20.GL_TRIANGLES);
        sp.end();
    }

    @Override
    public void dispose() {
        hexTexture.dispose();
        mesh.dispose();
        sp.dispose();
    }
}