package com.afqa123.intergalactic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.FontProvider;
import com.afqa123.intergalactic.data.Faction;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.data.Simulation;
import com.afqa123.intergalactic.graphics.ShaderFactory;
import com.afqa123.intergalactic.screens.GalaxyScreen;
import com.afqa123.intergalactic.screens.Screen;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Properties;
import java.util.Stack;

public class IntergalacticGame extends ApplicationAdapter {
	
    // The current screen
    private Screen screen;
    // Screen stack
    private final Stack<Screen> screens = new Stack<>();
    // The simulation engine
    private Simulation simulation;
    private Galaxy galaxy;
    private Faction player;
    private FPSLogger fps;
    private Properties labels;
    
	@Override
	public void create () {
        // Application settings
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // OpenGL settings
        // Disabled since it screws with effects
        //Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        //Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        fps = new FPSLogger();
        
        loadAssets();

        galaxy = new Galaxy(15);
        player = new Faction("Player", true, galaxy);       
        simulation = new Simulation(galaxy, player);
        // TODO: load from file, etc.
        simulation.init();

        //screen = new BloomTestScreen();
        //screen = new TestScreen(this);
        screen = new GalaxyScreen(this, galaxy);
        screen.activate();
    }

    private void loadAssets() {
        Gdx.app.log(IntergalacticGame.class.getName(), "Loading assets...");
        
        // Assets
        Assets.load("textures/nebula32.png", Texture.class);
        Assets.load("textures/base-red.png", Texture.class);
        Assets.load("textures/detail.png", Texture.class);
        Assets.load("textures/ui.png", Texture.class);        
        Assets.load("textures/explosion.png", Texture.class);        
        Assets.load("data/sectors.json", JsonValue.class);
        // TODO: load based on system locale
        Assets.load("localization/default.properties", Properties.class);
        
        // Scene shaders
        Assets.load("shaders/sc_color.vsh", String.class);
        Assets.load("shaders/sc_color.fsh", String.class);
        Assets.load("shaders/sc_sphere.vsh", String.class);
        Assets.load("shaders/sc_sphere.fsh", String.class);
        Assets.load("shaders/sc_star.vsh", String.class);
        Assets.load("shaders/sc_star.fsh", String.class);
        Assets.load("shaders/sc_star_noise.vsh", String.class);
        Assets.load("shaders/sc_star_noise.fsh", String.class);

        Assets.load("shaders/default.vsh", String.class);
        Assets.load("shaders/default.fsh", String.class);
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
        screen.update();        
        screen.render();
        if (screen.isDone()) {
            Gdx.app.exit();
        }
        fps.log();
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
    
    public void turn() {
        // TODO: implement AI turns
        simulation.turn();
    }
    
    public Properties getLabels() {
        return labels;
    }
    
    public Faction getPlayer() {
        return player;
    }
}