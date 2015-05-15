package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.data.Faction;
import com.afqa123.intergalactic.data.FactionMap;
import com.afqa123.intergalactic.data.FactionMap.SectorStatus;
import com.afqa123.intergalactic.data.FactionMap.Status;
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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;

public class GalaxyScreen extends AbstractScreen {

    private class GalaxyScreenInputProcessor extends InputAdapter {

        private static final int DRAG_RECT = 8;
        private static final float SCROLL_SPEED = 0.05f;
        private static final long DOUBLE_CLICK = 200l;
        private int lastX;
        private int lastY;
        private boolean dragging;
        private long lastUp;
        
        @Override
        public boolean keyDown(int i) {
            if (i == Input.Keys.ESCAPE) {
                setDone(true);
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
                    if (HexCoordinate.ORIGIN.getDistance(c) < galaxy.getRadius()) {
                        Gdx.app.log(GalaxyScreen.class.getName(), String.format("Sector: %d / %d", c.x, c.y));
                        long dt = TimeUtils.timeSinceMillis(lastUp);
                        // Regular click
                        if (dt > DOUBLE_CLICK) {
                            indicator.setPosition(c.toWorld());
                        // Double click
                        } else {
                            Sector sector = galaxy.getSector(c);
                            SectorStatus status = getGame().getPlayer().getMap().getSector(c);
                            if (sector.getCategory() != null && status.getStatus() == Status.EXPLORED) {
                                getGame().pushScreen(new SectorScreen(getGame(), sector));
                            }
                        }                    
                    }
                }
                lastUp = TimeUtils.millis();
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
    private final Galaxy galaxy;
    private final List<Sector> visibleSectors;
    
    // UI
    private final List<Label> sectorLabels;
    private final TextButton turnButton;
    
    public GalaxyScreen(IntergalacticGame game, Galaxy galaxy) {
        super(game);
        this.galaxy = galaxy;
        
        // Create ui components
        sectorLabels = new ArrayList<>();

        turnButton = new TextButton(getGame().getLabels().getProperty("BUTTON_TURN"), getSkin());
        turnButton.setPosition(STAGE_WIDTH - STAGE_MARGIN - turnButton.getWidth(), STAGE_MARGIN);
        turnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().turn();
            }
        });
        getStage().addActor(turnButton);
        
        cam = new PerspectiveCamera(42.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Center on player home
        Faction player = game.getPlayer();
        Sector home = player.getSectors().get(0);
        Vector3 target = home.getCoordinates().toWorld();
        cam.position.set(target.x, target.y + 10.0f, target.z + 5.0f);
        cam.lookAt(target);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        visibleSectors = new ArrayList<>();
        
        gridRenderer = new GridRenderer(galaxy);
        gridRenderer.update();        
        bgRenderer = new BackgroundRenderer();
        starRenderer = new StarRenderer();        
        indicator = new Indicator();        
        indicator.setPosition(target);
    }
    
    @Override
    public void activate() {
        super.activate();

        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(Gdx.input.getInputProcessor());
        m.addProcessor(new GalaxyScreenInputProcessor());
        Gdx.input.setInputProcessor(m);
        
        resetRenderLists();
    }

    @Override
    public void deactivate() {
        
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    // TODO: execute when Galaxy model changes -> more listeners!
    private void resetRenderLists() {
        visibleSectors.clear();
        for (Label l : sectorLabels) {
            l.remove();
        }
        sectorLabels.clear();
        
        FactionMap playerMap = getGame().getPlayer().getMap();
        List<Sector> sectors = galaxy.getStarSystems();
        for (Sector sector : sectors) {
            Status status = playerMap.getSector(sector.getCoordinates()).getStatus();
            if (status == Status.UNKNOWN) {
                continue;
            }

            Label sectorLabel = new Label(sector.getName(), getSkin(), FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
            sectorLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            getStage().addActor(sectorLabel);
            sectorLabel.setVisible(status == Status.EXPLORED);
            sectorLabels.add(sectorLabel);            
            visibleSectors.add(sector);
        }
    }
    
    @Override
    public void update() {
        // Create ui components
        for (int i = 0; i < visibleSectors.size(); i++) {
            Sector sector = visibleSectors.get(i);
            Vector3 screen = cam.project(sector.getCoordinates().toWorld());
            Label label = sectorLabels.get(i);
            label.setPosition(screen.x, screen.y + 40.0f * sector.getScale(), Align.bottom);
        }                
    }
    
    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);        
        bgRenderer.render(cam);        
        gridRenderer.render(cam);
        indicator.render(cam);

        starRenderer.render(cam, visibleSectors);
        
        // UI pass
        getStage().draw();
    }
    
    @Override
    public void dispose() {
        super.dispose();        
        gridRenderer.dispose();
        bgRenderer.dispose();
        starRenderer.dispose();
        indicator.dispose();
    }
}