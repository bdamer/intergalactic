package com.afqa123.intergalactic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.FontProvider;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.logic.Simulation;
import com.afqa123.intergalactic.graphics.ShaderFactory;
import com.afqa123.intergalactic.logic.generators.GalaxyGenerator;
import com.afqa123.intergalactic.screens.GalaxyScreen;
import com.afqa123.intergalactic.screens.Screen;
import com.afqa123.intergalactic.model.Session;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

public class IntergalacticGame extends ApplicationAdapter {
	
    public static final String PLAYER_FACTION = "player";
    
    // The current screen
    private Screen screen;
    // Screen stack
    private final Stack<Screen> screens = new Stack<>();
    // The simulation engine
    private Simulation simulation;
    // State of the game
    private Session session;
    
    //private Galaxy galaxy;
    //private Map<String,Faction> factions;
    //private Faction player;    
    private FPSLogger fps;
    private Properties labels;
    
	@Override
	public void create () {
        try {
            // Application settings
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
            // OpenGL settings
            // Disabled since it screws with effects
            //Gdx.gl.glEnable(GL20.GL_CULL_FACE);
            //Gdx.gl.glCullFace(GL20.GL_BACK);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            //fps = new FPSLogger();

            loadAssets();
            
            if (!loadAuto()) {
                GalaxyGenerator gen = new GalaxyGenerator();
                Galaxy galaxy = gen.generateSpiralGalaxy(15);
                Map<String,Faction> factions = new HashMap<>();
                // Color #c02020
                factions.put(PLAYER_FACTION, new Faction(PLAYER_FACTION, new Color(0.75f, 0.125f, 0.125f, 1.0f), true, galaxy));
                // Color #20a010 
                factions.put("ai", new Faction("ai", new Color(0.125f, 0.625f, 0.0625f, 1.0f), false, galaxy));
                session = new Session(galaxy, factions);
                simulation = new Simulation(session);
                simulation.init();
            }

            //screen = new BloomTestScreen();
            //screen = new TestScreen(this);
            screen = new GalaxyScreen(this);
            screen.activate();
        } catch (Throwable t) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error during init.", t);
            Gdx.app.exit();
        }
    }

    private void loadAssets() {
        Gdx.app.log(IntergalacticGame.class.getName(), "Loading assets...");
        
        // Assets
        Assets.load("textures/nebula32.png", Texture.class);
        Assets.load("textures/base-red.png", Texture.class);
        Assets.load("textures/catalog01.png", Texture.class);
        Assets.load("textures/detail.png", Texture.class);
        Assets.load("textures/explosion.png", Texture.class);
        Assets.load("data/plans/plan01.json", String.class);
        Assets.load("data/sectors.json", JsonValue.class);
        Assets.load("data/ships.json", String.class);
        Assets.load("data/stations.json", String.class);
        Assets.load("data/structures.json", String.class);
        
        // TODO: load based on system locale
        Assets.load("localization/default.properties", Properties.class);
        
        // Scene shaders
        Assets.load("shaders/sc_color.vsh", String.class);
        Assets.load("shaders/sc_color.fsh", String.class);
        Assets.load("shaders/sc_default.vsh", String.class);
        Assets.load("shaders/sc_default.fsh", String.class);
        Assets.load("shaders/sc_hexgrid.vsh", String.class);
        Assets.load("shaders/sc_hexgrid.fsh", String.class);
        Assets.load("shaders/sc_sphere.vsh", String.class);
        Assets.load("shaders/sc_sphere.fsh", String.class);
        Assets.load("shaders/sc_star.vsh", String.class);
        Assets.load("shaders/sc_star.fsh", String.class);
        Assets.load("shaders/sc_star_noise.vsh", String.class);
        Assets.load("shaders/sc_star_noise.fsh", String.class);

        Assets.load("shaders/textured.vsh", String.class);
        Assets.load("shaders/textured.fsh", String.class);
        Assets.load("shaders/transparency.fsh", String.class);
                
        // Effects Shaders
        Assets.load("shaders/fx_blur.fsh", String.class);
        Assets.load("shaders/fx_blur_h.vsh", String.class);
        Assets.load("shaders/fx_blur_v.vsh", String.class);
        Assets.load("shaders/fx_blur2.fsh", String.class);
        Assets.load("shaders/fx_default.vsh", String.class);
        Assets.load("shaders/fx_glow.fsh", String.class);
        Assets.load("shaders/fx_threshold.fsh", String.class);
        
        Assets.getManager().finishLoading();

        labels = Assets.get("localization/default.properties");
        
        FontProvider.intialize();
    }
    
    @Override
    public void resize(int width, int height) {
        Gdx.app.debug(IntergalacticGame.class.getName(), 
                String.format("Resizing screen to %d by %d", width, height));
        screen.resize(width, height);
    }
    
	@Override
	public void render () {
        try { 
            screen.update();        
            screen.render();
            if (screen.isDone()) {
                Gdx.app.exit();
            }
            if (fps != null) 
                fps.log();
        } catch (Throwable t) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error during rendering.", t);
            Gdx.app.exit();
        }
	}   

    @Override
    public void dispose() {
        FontProvider.free();
        screen.dispose();
        ShaderFactory.freeShaders();
    }
    
    public void pushScreen(Screen screen) {
        this.screen.deactivate();
        screens.push(this.screen);
        this.screen = screen;
        this.screen.activate();
    }
    
    public void popScreen() {
        this.screen.deactivate();
        this.screen.dispose();        
        this.screen = screens.pop();
        this.screen.activate();
    }
        
    public Properties getLabels() {
        return labels;
    }
    
    public Session getSession() {
        return session;
    }
    
    public Simulation getSimulation() {
        return simulation;
    }
    
    public void save(String filename) {
        try (JsonWriter writer = new JsonWriter(new FileWriter(filename))) {
            writer.setOutputType(JsonWriter.OutputType.json);
            Json json = new Json();
            json.toJson(session, writer);            
        } catch (IOException ex) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error saving file.", ex);
        }        
    }
    
    public void load(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            Json json = new Json();
            session = json.fromJson(Session.class, reader);
            simulation = new Simulation(session);
        } catch (IOException ex) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error loading file.", ex);            
        }
    }
    
    public void saveAuto() {
        // TODO: find proper location
        save("C:\\autosave.json");
    }
    
    public boolean loadAuto() {
        File f = new File("C:\\autosave.json");
        if (f.exists()) {
            load("C:\\autosave.json");
            return true;
        } else {
            return false;
        }
    }
}