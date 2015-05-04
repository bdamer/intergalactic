package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.data.Galaxy;
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

public class GalaxyScreen implements Screen {

    private class GalaxyScreenInputProcessor extends InputAdapter {

        private static final int DRAG_RECT = 8;
        private static final float SCROLL_SPEED = 0.05f;
        private int lastX;
        private int lastY;
        private boolean dragging;
        
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
            dragging = false;
            return true;                
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (!dragging) {
                Ray r = cam.getPickRay(screenX, screenY);
                // compute intersection with xz-plane
                final Vector3 normal = new Vector3(0.0f, 1.0f, 0.0f);
                float t = -r.origin.dot(normal) / r.direction.dot(normal);            
                if (t > 0) {
                    Vector3 hit = new Vector3(r.origin.x + r.direction.x * t,
                            r.origin.y + r.direction.y * t,
                            r.origin.z + r.direction.z * t);                
                    HexCoordinate c = new HexCoordinate(hit);
                    Gdx.app.log(GalaxyScreen.class.getName(), String.format("Sector: %d / %d", c.x, c.y));
                    indicator.setPosition(c.toWorld());
                }            
            }
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            // Check if we've moved enough to start dragging
            if (!dragging) {
                int dx = screenX - lastX;
                int dy = screenY - lastY;
                dragging = (dx * dx + dy * dy) >= DRAG_RECT;
            }
            
            if (dragging) {
                float dx = SCROLL_SPEED * (float)(screenX - lastX);
                float dy = SCROLL_SPEED * (float)(screenY - lastY);            
                cam.position.add(-dx, 0, -dy);
                cam.update();            
                lastX = screenX;
                lastY = screenY;
            }            
            return true;                
        }
    }
    
    private final PerspectiveCamera cam;
    private final GridRenderer gridRenderer;
    private final BackgroundRenderer bgRenderer;
    private final StarRenderer starRenderer;
    private final Indicator indicator;
    private boolean done;
    private final Galaxy galaxy;
    
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
        starRenderer = new StarRenderer();        
        indicator = new Indicator();
        
        this.galaxy = galaxy;
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
        starRenderer.render(cam, galaxy.getStarSystems());
    }
    
    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void dispose() {
        gridRenderer.dispose();
        bgRenderer.dispose();
        starRenderer.dispose();
        indicator.dispose();
    }
}