package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.graphics.BackgroundRenderer;
import com.afqa123.intergalactic.graphics.GridRenderer;
import com.afqa123.intergalactic.graphics.Indicator;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import java.util.ArrayList;
import java.util.List;

public class GalaxyScreen implements Screen {

    private class GalaxyScreenInputProcessor extends InputAdapter {
    
        private int lastX;
        private int lastY;
        private int lastButton;
        private final static float SCROLL_SPEED = 0.1f;
        
        @Override
        public boolean keyDown(int i) {
            if (i == Input.Keys.ESCAPE) {
                done = true;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            lastX = screenX;
            lastY = screenY;
            lastButton = button;
            if (button == 0) {
                Ray r = cam.getPickRay(screenX, screenY);            
                // compute intersection with xz-plane
                final Vector3 normal = new Vector3(0.0f, 1.0f, 0.0f);
                float t = -r.origin.dot(normal) / r.direction.dot(normal);            
                if (t > 0) {
                    Vector3 hit = new Vector3(r.origin.x + r.direction.x * t,
                            r.origin.y + r.direction.y * t,
                            r.origin.z + r.direction.z * t);                
                    HexCoordinate c = new HexCoordinate(hit);
                    indicator.setPosition(c.toWorld());
                }
                return true;                
            } else {
                return false;
            }            
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (lastButton == 1) {
                float dx = SCROLL_SPEED * (float)(screenX - lastX);
                float dy = SCROLL_SPEED * (float)(screenY - lastY);            
                cam.position.add(-dx, 0, -dy);
                cam.update();            
                lastX = screenX;
                lastY = screenY;
                return true;
            } else {
                return false;
            }
        }
    }
    
    private final PerspectiveCamera cam;
    private final GridRenderer gridRenderer;
    private final BackgroundRenderer bgRenderer;
    private final List<StarRenderer> starRenderers;
    private final Indicator indicator;
    private boolean done;
    
    public GalaxyScreen(Galaxy galaxy) {
	    cam = new PerspectiveCamera(45.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 10.0f, 5.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        gridRenderer = new GridRenderer(galaxy);
        gridRenderer.update();
        
        bgRenderer = new BackgroundRenderer();

        starRenderers = new ArrayList<>();
        for (Sector s : galaxy.getStarSystems()) {
            starRenderers.add(new StarRenderer(s));
        }
        
        indicator = new Indicator();
    }
    
    @Override
    public void activate() {
        done = false;

        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        Gdx.input.setInputProcessor(new GalaxyScreenInputProcessor());
    }

    @Override
    public void deactivate() {
        
    }
    
    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    @Override
    public void update() {
        
    }
    
    @Override
    public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);        
        bgRenderer.render(cam);        
        gridRenderer.render(cam);
        indicator.render(cam);
        for (StarRenderer r : starRenderers) {
            r.render(cam);
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void dispose() {
        gridRenderer.dispose();
        bgRenderer.dispose();
        for (StarRenderer r : starRenderers) {
            r.dispose();
        }
        indicator.dispose();
    }
}