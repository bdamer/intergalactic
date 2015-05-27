package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.data.Faction;
import com.afqa123.intergalactic.data.FactionMap;
import com.afqa123.intergalactic.data.FactionMap.SectorEntry;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.data.Range;
import com.afqa123.intergalactic.util.Path;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.data.SectorStatus;
import com.afqa123.intergalactic.data.Ship;
import com.afqa123.intergalactic.graphics.BackgroundRenderer;
import com.afqa123.intergalactic.graphics.GridRenderer;
import com.afqa123.intergalactic.graphics.Indicator;
import com.afqa123.intergalactic.graphics.PathRenderer;
import com.afqa123.intergalactic.graphics.ShipRenderer;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.afqa123.intergalactic.input.SmartInputAdapter;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import java.util.ArrayList;
import java.util.List;

public class GalaxyScreen extends AbstractScreen implements FactionMap.ChangeListener {

    private final static Vector3 CAMERA_OFFSET = new Vector3(0.0f, 10.0f, 5.0f);
    
    private class GalaxyScreenInputProcessor extends SmartInputAdapter {

        private static final float SCROLL_SPEED = 0.05f;

        @Override
        public boolean keyDown(int i) {
            switch (i) {
                case Input.Keys.ESCAPE:
                    setDone(true);
                    return true;
                case Input.Keys.ENTER:
                    getGame().turn();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDrag(int dx, int dy) {
            cam.position.add(SCROLL_SPEED * (float)-dx, 0, SCROLL_SPEED * (float)-dy);
            cam.update();
        }

        @Override
        public void onClick(int x, int y) {
            HexCoordinate c = pickSector(x, y);
            indicator.setPosition(c.toWorld());
        }

        @Override
        public void onLongClick(int x, int y) {
            HexCoordinate c = pickSector(x, y);
            // Testing for now...
            ship.setTarget(c);            
        }

        @Override
        public void onDoubleClick(int x, int y) {
            HexCoordinate c = pickSector(x, y);
            Sector sector = galaxy.getSector(c);
            SectorEntry entry = getGame().getPlayer().getMap().getSector(c);
            if (sector.getCategory() != null && entry.getStatus() == SectorStatus.EXPLORED) {
                getGame().pushScreen(new SectorScreen(getGame(), sector));
            }        
        }
        
        private HexCoordinate pickSector(int x, int y) {
            Ray r = cam.getPickRay(x, y);
            // compute intersection with xz-plane
            final Vector3 normal = new Vector3(0.0f, 1.0f, 0.0f);
            float t = -r.origin.dot(normal) / r.direction.dot(normal);            
            if (t > 0) {
                Vector3 hit = new Vector3(r.origin.x + r.direction.x * t,
                        r.origin.y + r.direction.y * t,
                        r.origin.z + r.direction.z * t);                
                HexCoordinate c = new HexCoordinate(hit);
                if (HexCoordinate.ORIGIN.getDistance(c) < galaxy.getRadius()) {
                    return c;
                }
            }
            return null;
        }
    }
    
    private final PerspectiveCamera cam;
    private final GridRenderer gridRenderer;
    private final BackgroundRenderer bgRenderer;
    private final StarRenderer starRenderer;
    private final Indicator indicator;
    private final Galaxy galaxy;
    private final List<Sector> visibleSectors;
    private final PathRenderer pathRenderer;
    private final Ship ship;
    private final ShipRenderer shipRenderer;
    
    // UI
    private final List<Label> sectorLabels;
    private final TextButton turnButton;
    
    public GalaxyScreen(IntergalacticGame game, Galaxy galaxy) {
        super(game);
        this.galaxy = galaxy;

        visibleSectors = new ArrayList<>();
        sectorLabels = new ArrayList<>();
        
        // Create ui components
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
        cam.position.set(target.x + CAMERA_OFFSET.x, target.y + CAMERA_OFFSET.y, target.z + CAMERA_OFFSET.z);
        cam.lookAt(target);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        gridRenderer = new GridRenderer(galaxy);
        bgRenderer = new BackgroundRenderer();
        starRenderer = new StarRenderer();        
        indicator = new Indicator();        
        indicator.setPosition(target);        
        pathRenderer = new PathRenderer();
        
        shipRenderer = new ShipRenderer();
        ship = new Ship("mother", Range.LONG, game.getPlayer());
        ship.setCoordinates(HexCoordinate.ORIGIN);
        game.getPlayer().getShips().add(ship);        
    }
    
    @Override
    public void activate() {
        super.activate();
        getGame().getPlayer().getMap().addChangeListener(this);

        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(Gdx.input.getInputProcessor());
        m.addProcessor(new GalaxyScreenInputProcessor());
        Gdx.input.setInputProcessor(m);
        
        mapChanged();
    }

    @Override
    public void deactivate() {
        getGame().getPlayer().getMap().removeChangeListener(this);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    @Override
    public void mapChanged() {
        visibleSectors.clear();
        for (Label l : sectorLabels) {
            l.remove();
        }
        sectorLabels.clear();
        
        FactionMap playerMap = getGame().getPlayer().getMap();
        List<Sector> sectors = galaxy.getStarSystems();
        for (Sector sector : sectors) {
            SectorStatus status = playerMap.getSector(sector.getCoordinates()).getStatus();
            if (status == SectorStatus.UNKNOWN) {
                continue;
            }

            Label sectorLabel = new Label(sector.getName(), getSkin(), FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
            sectorLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            getStage().addActor(sectorLabel);
            sectorLabel.setVisible(status == SectorStatus.EXPLORED);
            sectorLabels.add(sectorLabel);            
            visibleSectors.add(sector);
        }

        gridRenderer.update(playerMap);
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
        shipRenderer.render(cam, getGame().getPlayer().getShips());
        
        Path path = ship.getPath();
        if (path != null) {
            pathRenderer.render(cam, path);
        }
        
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