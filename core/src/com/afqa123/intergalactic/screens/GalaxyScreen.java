package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.Strings;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.FactionMap;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.SectorStatus;
import com.afqa123.intergalactic.logic.Simulation.StepListener;
import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.graphics.BackgroundRenderer;
import com.afqa123.intergalactic.graphics.BorderRenderer;
import com.afqa123.intergalactic.graphics.GridRenderer;
import com.afqa123.intergalactic.graphics.Indicator;
import com.afqa123.intergalactic.graphics.PathRenderer;
import com.afqa123.intergalactic.graphics.ShipRenderer;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.afqa123.intergalactic.graphics.StationRenderer;
import com.afqa123.intergalactic.input.SmartInputAdapter;
import com.afqa123.intergalactic.logic.UnitListener;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Notification;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.ShipType.Action;
import static com.afqa123.intergalactic.model.ShipType.Action.COLONIZE;
import com.afqa123.intergalactic.model.Station;
import com.afqa123.intergalactic.util.FactionBorder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;
import java.util.List;

public class GalaxyScreen extends AbstractScreen implements FactionMap.ChangeListener, StepListener, UnitListener {

    private static final Vector3 CAMERA_OFFSET = new Vector3(0.0f, 10.0f, 5.0f);
    private static final float SCROLL_SPEED = 0.05f;
    private static final Color DEFAULT_SECTOR_COLOR = Color.BLACK;

    private class DesktopInputProcessor extends InputAdapter {
        
        private static final int DEFAULT_DRAG_THRESHOLD = 8;
        private final int dragThreshold = DEFAULT_DRAG_THRESHOLD;
        private boolean dragging;
        private int touchX;
        private int touchY;        
        private boolean leftDown;
        private boolean rightDown;        
        private int lastX;
        private int lastY;

        @Override
        public boolean keyDown(int i) {
            switch (i) {
                case Input.Keys.ESCAPE:
                    setDone(true);
                    return true;
                case Input.Keys.ENTER:
                    getGame().getSimulation().turn();
                    return true;
                // Colonizes a planet
                case Input.Keys.C:
                    if (activeShip != null && activeShip.colonizeSector(getSession())) {
                        selectShip(null);
                        processNotifications();
                    }
                    return true;                    
                // Builds an outpost
                case Input.Keys.O:
                    if (activeShip != null && activeShip.buildStation(getSession())) {
                        selectShip(null);
                    }
                    return true;
                // Kill current unit
                case Input.Keys.K:
                    if (activeShip != null) {
                        // TODO: needs to update faction map 
                        getSession().destroyUnit(activeShip);
                        selectShip(null);
                    }
                    return true;
                case Input.Keys.F:
                    if (activeShip != null) {
                        activeShip.fortify();
                        selectShip(null);
                    }
                    return true;
                case Input.Keys.S:
                    getGame().getStateManager().saveAuto(getSession());
                    return true;
                case Input.Keys.F1:
                    debugDeityMode = !debugDeityMode;
                    mapChanged();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            lastX = touchX = x;
            lastY = touchY = y;
            dragging = false;
            if (button == Input.Buttons.LEFT) {
                leftDown = true;
                return true;
            } else if (button == Input.Buttons.RIGHT) {
                rightDown = true;
                selectTarget(x, y);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean touchDragged(int x, int y, int pointer) {
            if (leftDown) {
                // Check if we've moved enough to start dragging
                if (!dragging) {
                    int dx = x - touchX;
                    int dy = y - touchY;
                    dragging = (dx * dx + dy * dy) >= dragThreshold;
                }
                if (dragging) {
                    cam.position.add(SCROLL_SPEED * (float)(lastX - x), 0, SCROLL_SPEED * (float)(lastY - y));
                    cam.update();
                    lastX = x;
                    lastY = y;
                }
                return true;
            } else if (rightDown) {
                selectTarget(x, y);
                return true;
            } else {
                return false;
            }            
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                leftDown = false;
                HexCoordinate c = pickSector(x, y);
                for (Ship s : getSession().getPlayer().getShips()) {
                    // unit needs to be in sector and be owned by player
                    if (s.getCoordinates().equals(c) && s != activeShip) {
                        selectShip(s);
                        break;
                    }
                }
                return true;
            } else if (button == Input.Buttons.RIGHT) {
                rightDown = false;
                if (activeShip != null && activeShip.hasPath()) {
                    activeShip.move(getSession());
                    if (activeShip != null) {
                        indicator.setPosition(activeShip.getCoordinates().toWorld());
                    } else {
                        indicator.setPosition(null);
                    }
                    processNotifications();
                }
                return true;
            } else {
                return false;
            }
        }
    };
    
    private class MobileInputProcessor extends SmartInputAdapter {

        // TODO: not needed for mobile
        @Override
        public boolean keyDown(int i) {
            switch (i) {
                case Input.Keys.ESCAPE:
                    setDone(true);
                    return true;
                case Input.Keys.ENTER:
                    getGame().getSimulation().turn();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDrag(int dx, int dy, int button) {
            cam.position.add(SCROLL_SPEED * (float)-dx, 0, SCROLL_SPEED * (float)-dy);
            cam.update();
        }

        @Override
        public void onClick(int x, int y, int button) {
            HexCoordinate c = pickSector(x, y);
            indicator.setPosition(c.toWorld());
        }

        @Override
        public void onLongClick(int x, int y, int button) {
            HexCoordinate c = pickSector(x, y);
            // Testing for now...
            if (activeShip != null) {
                activeShip.selectTarget(getSession(), c);            
            }
        }

        @Override
        public void onDoubleClick(int x, int y, int button) {
            
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
    private final ShipRenderer shipRenderer;
    private final StationRenderer stationRenderer;
    private final BorderRenderer innerBorderRenderer;
    private final BorderRenderer outerBorderRenderer;
    private Ship activeShip;
    private boolean debugDeityMode;
    
    // UI
    private final List<Label> sectorLabels;
    private final TextButton turnButton;
    private final Image shipFortifyButton;
    private final Image shipDisbandButton;
    private final Image shipColonizeButton;
    private final Image shipStationButton;
    
    public GalaxyScreen(IntergalacticGame game) {
        super(game);
        this.galaxy = getSession().getGalaxy();

        visibleSectors = new ArrayList<>();
        sectorLabels = new ArrayList<>();
        
        // Create ui components
        turnButton = new TextButton(Strings.get("BUTTON_TURN"), getSkin());
        turnButton.setPosition(STAGE_WIDTH - STAGE_MARGIN - turnButton.getWidth(), STAGE_MARGIN);
        turnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getSimulation().turn();
            }
        });
        getStage().addActor(turnButton);
        
        Texture texture = Assets.get("textures/catalog01.png");
        shipFortifyButton = new Image(new TextureRegion(texture, 0.0625f, 0.125f, 0.125f, 0.1875f));
        shipFortifyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (activeShip != null) {
                    activeShip.fortify();
                    selectShip(null);
                }
            }
        });
        shipFortifyButton.setVisible(false);
        getStage().addActor(shipFortifyButton);

        shipDisbandButton = new Image(new TextureRegion(texture, 0.1875f, 0.125f, 0.25f, 0.1875f));        
        shipDisbandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (activeShip != null) {
                    // TODO: needs to update faction map 
                    getSession().destroyUnit(activeShip);
                    selectShip(null);
                }
            }
        });
        shipDisbandButton.setVisible(false);
        getStage().addActor(shipDisbandButton);
        
        shipColonizeButton = new Image(new TextureRegion(texture, 0.0f, 0.125f, 0.0625f, 0.1875f));    
        shipColonizeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (activeShip != null && activeShip.colonizeSector(getSession())) {
                    selectShip(null);
                    processNotifications();
               }
            }
        });
        shipColonizeButton.setVisible(false);
        getStage().addActor(shipColonizeButton);

        shipStationButton = new Image(new TextureRegion(texture, 0.125f, 0.125f, 0.1875f, 0.1875f));        
        shipStationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (activeShip != null && activeShip.buildStation(getSession())) {
                    selectShip(null);
                }
            }
        });
        shipStationButton.setVisible(false);
        getStage().addActor(shipStationButton);
        
        cam = new PerspectiveCamera(38.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Center on player home
        Faction player = getSession().getPlayer();
        Sector home = galaxy.getFactionSystems(player).get(0);
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
        pathRenderer = new PathRenderer();
        
        shipRenderer = new ShipRenderer();        
        stationRenderer = new StationRenderer();
        innerBorderRenderer = new BorderRenderer(new Color(0.0f, 1.0f, 0.0f, 0.5f));
        outerBorderRenderer = new BorderRenderer(new Color(1.0f, 0.0f, 0.0f, 0.5f));
    }
    
    @Override
    public void activate() {
        super.activate();
        getSession().getPlayer().getMap().addChangeListener(this);
        getSession().addUnitListener(this);
        getGame().getSimulation().addStepListener(this);

        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(Gdx.input.getInputProcessor());
        m.addProcessor(new DesktopInputProcessor());
        Gdx.input.setInputProcessor(m);
        
        mapChanged();
    }

    @Override
    public void deactivate() {
        getSession().getPlayer().getMap().removeChangeListener(this);
        getSession().removeUnitListener(this);
        getGame().getSimulation().removeStepListener(this);
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

        // TODO: do this once, not every time the map changes
        Texture texture = Assets.get("textures/catalog01.png");
        float size = 16.0f / 1024.0f;
        TextureRegion tr = new TextureRegion(texture, 0.125f, 0.0f, 0.125f + size, size);
        Drawable labelBackground = new TextureRegionDrawable(tr);
        LabelStyle defaultLabelStyle = new LabelStyle(getSkin().getFont(FONT), DEFAULT_SECTOR_COLOR);
        defaultLabelStyle.background = labelBackground;
        
        final Faction player = getSession().getPlayer();
        final FactionMap playerMap = player.getMap();
        List<Sector> sectors = galaxy.getStarSystems();
        for (final Sector sector : sectors) {
            SectorStatus status = playerMap.getSector(sector.getCoordinates()).getStatus();
            if (!debugDeityMode && status == SectorStatus.UNKNOWN) {
                continue;
            }

            // Create label based on sector owner
            LabelStyle style;
            if (sector.hasOwner()) {
                Faction owner = getSession().getFactions().get(sector.getOwner());
                style = new LabelStyle(getSkin().getFont(FONT), owner.getColor());
                style.background = labelBackground;            
            } else {
                style = defaultLabelStyle;
            }
            
            Label sectorLabel = new Label(sector.getName(), style);
            sectorLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Move to sector screen if this is a player colony
                    if (sector.getType() != null && 
                        (debugDeityMode || Faction.PLAYER_FACTION.equals(sector.getOwner()))) {
                        getGame().pushScreen(new SectorScreen(getGame(), sector));
                    }
                }
            });
            getStage().addActor(sectorLabel);
            sectorLabel.setVisible(debugDeityMode || (status == SectorStatus.EXPLORED));
            sectorLabels.add(sectorLabel);            
            visibleSectors.add(sector);
        }

        gridRenderer.update(getSession());
        
        FactionBorder borders = new FactionBorder(playerMap);
        innerBorderRenderer.update(borders.getInnerBorder());
        outerBorderRenderer.update(borders.getOuterBorder());
    }
    
    @Override
    public boolean prepareStep() {
        Session session = getSession();
        List<Ship> ships = session.getPlayer().getShips();
        int i = 0;
        while (i < ships.size()) {
            Ship s = ships.get(i);
            // check if unit is done for this turn
            if (!s.isReadyForUpdate()) {
                // if not, select it and see if it has a path. if it doesn't 
                // have a path, force player interaction, otherwise continue
                // moving along path.
                selectShip(s);
                if (!s.hasPath()) {
                    return false;
                }
                s.move(session);
                processNotifications();
            } else {
                // only increment if unit was ready, otherwise we'll check again 
                // during the next iteration
                i++;
            }
        }        
        // Check that all sectors are ready
        for (Sector s : galaxy.getFactionSystems(session.getPlayer())) {
            if (s.isIdle()) {
                focusCamera(s.getCoordinates().toWorld());
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void afterStep() {
        processNotifications();
        if (activeShip != null) {
            indicator.setPosition(activeShip.getCoordinates().toWorld());
        } else {
            indicator.setPosition(null);
        }
        getGame().getStateManager().saveAuto(getSession());
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

        // disable depth testing so we can use transparency
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        starRenderer.render(cam, visibleSectors);
        gridRenderer.render(cam);
        indicator.render(cam);
        innerBorderRenderer.render(cam);
        outerBorderRenderer.render(cam);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        
        // Create unit render lists
        FactionMap playerMap = getSession().getPlayer().getMap();
        List<Ship> ships = new ArrayList<>();
        List<Station> stations = new ArrayList<>();
        for (Unit u : getSession().getUnits()) {
            if (u instanceof Ship) {
                Ship ship = (Ship)u;
                if (debugDeityMode) {
                    ships.add(ship);
                } else {
                    SectorStatus status = playerMap.getSector(ship.getCoordinates()).getStatus();
                    if (status == SectorStatus.EXPLORED || status == SectorStatus.KNOWN) {
                        ships.add(ship);
                    }
                }
            } else if (u instanceof Station) {
                Station station = (Station)u;
                if (debugDeityMode) {
                    stations.add(station);
                } else {
                    SectorStatus status = playerMap.getSector(station.getCoordinates()).getStatus();
                    if (status == SectorStatus.EXPLORED || status == SectorStatus.KNOWN) {
                        stations.add(station);
                    }                    
                }             
            }
        }
        shipRenderer.render(cam, ships);
        stationRenderer.render(cam, stations);
        
        if (activeShip != null) {
            pathRenderer.render(cam, activeShip);
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
        shipRenderer.dispose();
        stationRenderer.dispose();
        indicator.dispose();
        innerBorderRenderer.dispose();
        outerBorderRenderer.dispose();
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
                Gdx.app.log(GalaxyScreen.class.getName(), String.format("Sector: %s", c));
                return c;
            }
        }
        return null;
    }
    
    private void selectTarget(int x, int y) {
        if (activeShip == null) {
            return;
        }                
        // check if we're looking at new sector and need to recompute path
        HexCoordinate c = pickSector(x, y);
        if (c == null || c.equals(activeShip.getTarget())) {
            return;
        }
        activeShip.selectTarget(getSession(), c);
    }
    
    private void selectShip(Ship ship) {
        activeShip = ship;

        if  (activeShip != null) {
            Vector3 target = activeShip.getCoordinates().toWorld();
            indicator.setPosition(target);
            focusCamera(target);
            activeShip.wake();            

            float offset = 0.0f;
            shipFortifyButton.setVisible(true);
            shipFortifyButton.setPosition(offset, STAGE_MARGIN);
            offset += shipFortifyButton.getWidth();
            shipDisbandButton.setVisible(true);
            shipDisbandButton.setPosition(offset, STAGE_MARGIN);
            offset += shipDisbandButton.getWidth();
        
            // Hide buttons
            shipColonizeButton.setVisible(false);                
            shipStationButton.setVisible(false);                
            
            for (Action action : activeShip.getActions()) {
                switch (action) {
                    case COLONIZE:
                        // TODO: check activeShip.canPerformAction(ShipType.Action.COLONIZE) to 
                        // see if button is "enabled"
                        shipColonizeButton.setVisible(true);
                        shipColonizeButton.setPosition(offset, STAGE_MARGIN);
                        offset += shipColonizeButton.getWidth();
                        break;
                        
                    case BUILD_STATION:
                        // TODO: check activeShip.canPerformAction(ShipType.Action.BUILD_STATION) to
                        // see if button is "enabled"
                        shipStationButton.setVisible(true);
                        shipStationButton.setPosition(offset, STAGE_MARGIN);
                        offset += shipStationButton.getWidth();
                        break;
                }
            }
        } else {
            indicator.setPosition(null);
            shipFortifyButton.setVisible(false);
            shipDisbandButton.setVisible(false);
            shipColonizeButton.setVisible(false);
            shipStationButton.setVisible(false);
        }        
    }
    
    private void focusCamera(Vector3 target) {
        cam.position.set(target.x + CAMERA_OFFSET.x, target.y + CAMERA_OFFSET.y, target.z + CAMERA_OFFSET.z);
        cam.lookAt(target);
        cam.update();
    }
    
    private void processNotifications() {
        while (getSession().hasNotifications()) {
            Notification n = getSession().pollNotifications();
            focusCamera(n.getFocus().toWorld());
                    
            final Dialog dlg = new Dialog(n.getTitle(), getSkin()) {
                @Override
                protected void result(Object object) {
                    remove();
                }
            };
            
            dlg.button(Strings.get("BUTTON_OK"), true);
            dlg.key(Input.Keys.ENTER, true);
            
            LabelStyle style = new LabelStyle(getSkin().getFont(FONT), Color.WHITE);
            Label label = new Label(n.getMessage(), style);
            label.setAlignment(Align.center);
            dlg.getContentTable().add(label);
            dlg.setModal(true);
            dlg.setMovable(false);
            dlg.show(getStage());
        }
    }

    @Override
    public void unitCreated(Unit u) {
        
    }

    @Override
    public void unitDestroyed(Unit u) {
        if (u == activeShip) {
            selectShip(null);
        }
    }
}