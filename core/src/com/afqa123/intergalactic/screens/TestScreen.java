package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.graphics.CubeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class TestScreen extends AbstractScreen {

    public class TestInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int i) {
            switch (i) {
                case Keys.ESCAPE:
                    setDone(true);
                    return true;
                default:          
                    return false;
            }
        }
    } 
    
    private final PerspectiveCamera cam;
    private final CameraInputController camCtrl;
    private CubeRenderer renderer;

    // Test objects here
    
    public TestScreen(IntergalacticGame game) {
        super(game);
        
	    cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 0.0f, 10.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.1f;
        cam.far = 100.0f;
        cam.update();

        camCtrl = new CameraInputController(cam);        
        renderer = new CubeRenderer();
        
        // Test code here:
              
    }
    
    @Override
    public void activate() {
        super.activate();
        
        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        InputMultiplexer mp = new InputMultiplexer();
        mp.addProcessor(Gdx.input.getInputProcessor());
        mp.addProcessor(new TestInputProcessor());
        mp.addProcessor(camCtrl);
        Gdx.input.setInputProcessor(mp);
    }

    @Override
    public void deactivate() {
    
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update(true);
    }

    @Override
    public void update() {
        camCtrl.update();   
    }
    
    @Override
    public void render() {
        // Scene pass
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderer.render(cam);        

        // UI pass
        getStage().draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
    }
}