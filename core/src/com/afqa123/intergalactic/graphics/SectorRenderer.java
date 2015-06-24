package com.afqa123.intergalactic.graphics;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.entities.Sector;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class SectorRenderer implements Disposable {
 
    private static final int SLICES = 32;
    private static final int STACKS = 24;
    private final ShaderProgram sp;
    private final Texture explosion;
    private final Sector sector;
    private final Mesh mesh;        
    
    public SectorRenderer(Sector sector) {
        this.sector = sector;
        sp = ShaderFactory.buildShader("shaders/sc_star_noise.vsh", "shaders/sc_star_noise.fsh");
        explosion = Assets.get("textures/explosion.png");
        mesh = new MeshBuilder().buildSphere(SLICES, STACKS);
    }
 
    public void render(Camera cam) {
        Matrix4 mvp = new Matrix4();
        Matrix4 model = new Matrix4();
        
        explosion.bind(0);
        sp.begin();
        sp.setUniformi("u_tex0", 0);

        float scale = sector.getScale();
        model.setToTranslationAndScaling(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(scale, scale, scale));            
        mvp.set(cam.combined);
        mvp.mul(model);

        sp.setUniformMatrix("u_mvp", mvp);
        sp.setUniformf("u_time", sector.getTurbulence() * (System.currentTimeMillis() - sector.getSeed()) );
        sp.setUniformf("u_gradient", sector.getGradient());                
        mesh.render(sp, GL20.GL_TRIANGLE_STRIP);
        
        sp.end();
    }

    @Override
    public void dispose() {
        mesh.dispose();
        sp.dispose();
        explosion.dispose();
    }
    
}
