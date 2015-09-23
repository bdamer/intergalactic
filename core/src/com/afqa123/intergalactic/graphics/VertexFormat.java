package com.afqa123.intergalactic.graphics;

import static com.afqa123.intergalactic.graphics.MeshBuilder.ATTR_COL4;
import static com.afqa123.intergalactic.graphics.MeshBuilder.ATTR_NOR3;
import static com.afqa123.intergalactic.graphics.MeshBuilder.ATTR_POS3;
import static com.afqa123.intergalactic.graphics.MeshBuilder.ATTR_UV2;
import com.badlogic.gdx.graphics.VertexAttribute;

public enum VertexFormat {
        
    POS3(3, new VertexAttribute[] { ATTR_POS3 }),
    POS3_NOR3(6, new VertexAttribute[] { ATTR_POS3, ATTR_NOR3 }),
    POS3_COL4(7, new VertexAttribute[] { ATTR_POS3, ATTR_COL4 }),
    POS3_UV2(5, new VertexAttribute[] { ATTR_POS3, ATTR_UV2 }),
    POS3_NOR3_UV2(8, new VertexAttribute[] { ATTR_POS3, ATTR_NOR3, ATTR_UV2 });

    private final int size;
    private final VertexAttribute[] attributes;

    private VertexFormat(int size, VertexAttribute[] attributes) {
        this.size = size;
        this.attributes = attributes;
    }

    public int size() {
        return size;
    }

    public VertexAttribute[] getAttributes() {
        return attributes;
    }
}
