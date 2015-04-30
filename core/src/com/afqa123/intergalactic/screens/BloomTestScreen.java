package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.graphics.ShaderFactory;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class BloomTestScreen implements Screen {

    public class TestInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int i) {
            switch (i) {
                case Keys.ESCAPE:
                    done = true;
                    return true;
                case Keys.NUM_1:    // just render scene
                    mode = 1;
                    return true;
                case Keys.NUM_2:    // add threshold pass
                    mode = 2;
                    return true;
                case Keys.NUM_3:    // add horizontal blur
                    mode = 3;
                    return true;
                case Keys.NUM_4:    // add vertical blur
                    mode = 4;
                    return true;
                case Keys.F1:       // regular blur 
                    alernateBlur = false;
                    return true;
                case Keys.F2:        // alternate blur
                    alernateBlur = true;
                    return true;
                default:                     
                    return false;
            }
        }
    } 
    
    private final PerspectiveCamera cam;
    private final CameraInputController camCtrl;
    private boolean done;
//    private CubeRenderer renderer;

    // Test objects here
    private StarRenderer renderer;
    private final ShaderProgram compShader;
    private final ShaderProgram thresholdShader;
    private final ShaderProgram hblurShader;
    private final ShaderProgram vblurShader;
    private final ShaderProgram blurShader2;
    private final Mesh mesh;
    
    // Size of FBO for effects stages
    private static final int FBO_SIZE = 256;
    private FrameBuffer fbo0;
    private FrameBuffer fbo1;
    private FrameBuffer fbo2;
    private int mode = 4;
    private boolean alernateBlur;
    
    public BloomTestScreen() {
	    cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 0.0f, 10.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.1f;
        cam.far = 100.0f;
        cam.update();

        camCtrl = new CameraInputController(cam);        
//        renderer = new CubeRenderer();
        
        // Test code here:
        Sector sector = new Sector(new HexCoordinate(0,0), Sector.StarCategory.RED);
        renderer = new StarRenderer(sector);

        fbo0 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
        
        compShader = ShaderFactory.buildShader("shaders/fx_default.vsh", "shaders/fx_glow.fsh");
        thresholdShader = ShaderFactory.buildShader("shaders/fx_default.vsh", "shaders/fx_threshold.fsh");
        hblurShader = ShaderFactory.buildShader("shaders/fx_blur_h.vsh", "shaders/fx_blur.fsh");
        vblurShader = ShaderFactory.buildShader("shaders/fx_blur_v.vsh", "shaders/fx_blur.fsh");
        blurShader2 = ShaderFactory.buildShader("shaders/fx_default.vsh", "shaders/fx_blur2.fsh");
        
        // Simple mesh to render fullscreen texture to during fx pass
        mesh = new Mesh(true, 4, 0, new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
        /**
         * 2 3
         * 0 1
         */
        float[] vertices = new float[] {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f            
        };        
        mesh.setVertices(vertices);
    }
    
    @Override
    public void activate() {
        done = false;
        
        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        InputMultiplexer mp = new InputMultiplexer();
        mp.addProcessor(camCtrl);
        mp.addProcessor(new TestInputProcessor());
        Gdx.input.setInputProcessor(mp);
    }

    @Override
    public void deactivate() {
    
    }

    @Override
    public void update() {
    
    }

    private FrameBuffer getNextFbo(FrameBuffer lastFbo) {
        if (lastFbo == fbo0 || lastFbo == fbo2) {
            return fbo1;
        } else {
            return fbo2;
        }
    }
        
    private FrameBuffer thresholdPass(FrameBuffer lastFbo) {
        FrameBuffer nextFbo = getNextFbo(lastFbo);
        nextFbo.begin();
        Gdx.gl.glViewport(0, 0, nextFbo.getWidth(), nextFbo.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lastFbo.getColorBufferTexture().bind(0);
        thresholdShader.begin();
        thresholdShader.setUniformi("u_tex0", 0);
        thresholdShader.setUniformf("u_scale", 0.02f);
        thresholdShader.setUniformf("u_threshold", 0.1f);
        mesh.render(thresholdShader, GL20.GL_TRIANGLE_STRIP);
        thresholdShader.end(); 
        nextFbo.end();
        return nextFbo;        
    }
    
    private FrameBuffer horizontalBlur(FrameBuffer lastFbo) {
        FrameBuffer nextFbo = getNextFbo(lastFbo);
        nextFbo.begin();
        Gdx.gl.glViewport(0, 0, nextFbo.getWidth(), nextFbo.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lastFbo.getColorBufferTexture().bind(0);
        if (alernateBlur) {
            hblurShader.begin();
            hblurShader.setUniformi("u_tex0", 0);
            mesh.render(hblurShader, GL20.GL_TRIANGLE_STRIP);
            hblurShader.end();             
        } else {
            blurShader2.begin();
            blurShader2.setUniformi("u_tex0", 0);
            blurShader2.setUniformf("u_blur", 1.5f / (float)FBO_SIZE);
            blurShader2.setUniformf("u_dir", new Vector2(1.0f, 0.0f));
            mesh.render(blurShader2, GL20.GL_TRIANGLE_STRIP);
            blurShader2.end(); 
        }
        nextFbo.end();
        return nextFbo;
    }
    
    private FrameBuffer verticalBlur(FrameBuffer lastFbo) {
        FrameBuffer nextFbo = getNextFbo(lastFbo);
        nextFbo.begin();
        Gdx.gl.glViewport(0, 0, nextFbo.getWidth(), nextFbo.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lastFbo.getColorBufferTexture().bind(0);
        if (alernateBlur) {
            vblurShader.begin();
            vblurShader.setUniformi("u_tex0", 0);
            mesh.render(vblurShader, GL20.GL_TRIANGLE_STRIP);
            vblurShader.end();             
        } else {
            blurShader2.begin();
            blurShader2.setUniformi("u_tex0", 0);
            blurShader2.setUniformf("u_blur", 2.0f / (float)FBO_SIZE);
            blurShader2.setUniformf("u_dir", new Vector2(0.0f, 1.0f));
            mesh.render(blurShader2, GL20.GL_TRIANGLE_STRIP);
            blurShader2.end();
        }
        nextFbo.end();
        return nextFbo;
    }
    
    @Override
    public void render() {
        camCtrl.update();
        
        // Scene pass
        fbo0.begin();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderer.render(cam);
        fbo0.end();
        
        FrameBuffer lastFbo = fbo0;        
        // Effects passes...
        if (mode > 1) {
            // Threshold pass
            lastFbo = thresholdPass(lastFbo);
            
            if (mode > 2) {
                // Horizontal blur pass
                lastFbo = horizontalBlur(lastFbo);
            
                if (mode > 3) {
                    // Vertical blur pass
                    lastFbo = verticalBlur(lastFbo);                    
                }
            }
        }

        // Composite pass
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        fbo0.getColorBufferTexture().bind(0);
        lastFbo.getColorBufferTexture().bind(1);
        compShader.begin();
        compShader.setUniformi("u_tex0", 0);
        compShader.setUniformi("u_tex1", 1);
        compShader.setUniformf("u_scale", 0.0f);
        mesh.render(compShader, GL20.GL_TRIANGLE_STRIP);
        compShader.end();        
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update(true);

        if (fbo0 != null) {
            fbo0.dispose();
        }
        fbo0 = new FrameBuffer(Pixmap.Format.RGBA8888, 
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void dispose() {
        renderer.dispose();
        fbo0.dispose();
        fbo1.dispose();
        fbo2.dispose();
    }
}