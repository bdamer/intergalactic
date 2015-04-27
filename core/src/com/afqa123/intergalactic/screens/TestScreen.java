package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.graphics.CubeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class TestScreen implements Screen {

    public class TestInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int i) {
            if (i == Keys.ESCAPE) {
                done = true;
                return true;
            } else {
                return false;
            }
        }
    } 
    
    private final PerspectiveCamera cam;
    private final CameraInputController camCtrl;
    private boolean done;
    private CubeRenderer renderer;
        
    public TestScreen() {
	    cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 0.0f, 10.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();

        camCtrl = new CameraInputController(cam);       
        
        renderer = new CubeRenderer();
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

    @Override
    public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);      
        
        renderer.render(cam);
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}