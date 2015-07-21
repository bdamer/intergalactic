package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.math.Hex;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.SectorStatus;
import com.afqa123.intergalactic.model.Session;
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

public class GridRenderer implements Disposable {

    // Offset for regular hex
    private static final Vector2 TEXTURE_OFFSET1 = new Vector2(0.0f, 0.25f);
    // Offset for bigger hex used to indicate owned sectors
    private static final Vector2 TEXTURE_OFFSET2 = new Vector2(0.25f, 0.25f);
    private static final Vector2 TEXTURE_SIZE = new Vector2(0.25f, 0.25f);
    // Number of vertices per hex
    private static final int NUM_VERTICES = 4;
    // Number of indices per hex
    private static final int NUM_INDICES = 6;
    // Number of elements per vertex
    private static final int VERTEX_SIZE = 9;
    private static final Color GRID_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.5f);
    
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
            new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE),
            new VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
    }
    
    /**
     * Rebuilds the grid mesh.
     * 
     * @param session The game session.
     */
    public void update(Session session) {
        // TODO: use lists (although ideally, we'd only want to update the
        // vertices that have changed)
        float[] vertices = new float[galaxy.getCount() * NUM_VERTICES * VERTEX_SIZE];
        short[] indices = new short[galaxy.getCount() * NUM_INDICES];
        
        int counter = 0;
        Sector[][] sectors = galaxy.getSectors();
        FactionMapSector[][] entries = session.getPlayer().getMap().getSectors();
        for (int row = 0; row < sectors.length; row++) {
            for (int col = 0; col < sectors[row].length; col++) {
                Sector sector = sectors[row][col];
                // determine visiblity of sector based on player map
                FactionMapSector factionSector = entries[row][col];
                if (factionSector.getStatus() == SectorStatus.UNKNOWN) {
                    continue;
                }
                
                // TODO: use player faction map to determine any additional properties
                Color c;
                Vector2 to;
                if (sector.hasOwner()) {
                    Faction owner = session.getFactions().get(sector.getOwner());
                    c = owner.getColor();
                    to = TEXTURE_OFFSET2;
                } else {
                    c = GRID_COLOR;
                    to = TEXTURE_OFFSET1;
                }
                
                addSector(sector.getCoordinates().toWorld(), c, to, vertices, indices, counter);
                counter++;
            }
        }
        
        // Update mesh
        mesh.setVertices(vertices, 0, counter * NUM_VERTICES * VERTEX_SIZE);
        mesh.setIndices(indices, 0, counter * NUM_INDICES);
        
        //Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh vertices: %d", mesh.getNumVertices()));
        //Gdx.app.debug(GridRenderer.class.getName(), String.format("Mesh indices: %d", mesh.getNumIndices()));
    }
    
    private void addSector(Vector3 pos, Color color, Vector2 texOffset, float[] vertices, short[] indices, int vCount) {
        int vIndex = vCount * NUM_VERTICES * VERTEX_SIZE;
        // Vertex order:
        // 2 3
        // 0 1

        // Vertex 0
        vertices[vIndex++] = pos.x - Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.SIZE;
        vertices[vIndex++] = texOffset.x;
        vertices[vIndex++] = texOffset.y + TEXTURE_SIZE.y;
        vertices[vIndex++] = color.r;
        vertices[vIndex++] = color.g;
        vertices[vIndex++] = color.b;
        vertices[vIndex++] = color.a;
        
        // Vertex 1
        vertices[vIndex++] = pos.x + Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z + Hex.SIZE;
        vertices[vIndex++] = texOffset.x + TEXTURE_SIZE.x;
        vertices[vIndex++] = texOffset.y + TEXTURE_SIZE.y;
        vertices[vIndex++] = color.r;
        vertices[vIndex++] = color.g;
        vertices[vIndex++] = color.b;
        vertices[vIndex++] = color.a;

        // Vertex 2
        vertices[vIndex++] = pos.x - Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.SIZE;
        vertices[vIndex++] = texOffset.x;
        vertices[vIndex++] = texOffset.y;
        vertices[vIndex++] = color.r;
        vertices[vIndex++] = color.g;
        vertices[vIndex++] = color.b;
        vertices[vIndex++] = color.a;

        // Vertex 3
        vertices[vIndex++] = pos.x + Hex.SIZE;
        vertices[vIndex++] = 0.0f;
        vertices[vIndex++] = pos.z - Hex.SIZE;
        vertices[vIndex++] = texOffset.x + TEXTURE_SIZE.x;
        vertices[vIndex++] = texOffset.y;
        vertices[vIndex++] = color.r;
        vertices[vIndex++] = color.g;
        vertices[vIndex++] = color.b;
        vertices[vIndex++] = color.a;
    
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