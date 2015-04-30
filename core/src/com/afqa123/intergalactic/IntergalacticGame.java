package com.afqa123.intergalactic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.graphics.ShaderFactory;
import com.afqa123.intergalactic.screens.BloomTestScreen;
import com.afqa123.intergalactic.screens.GalaxyScreen;
import com.afqa123.intergalactic.screens.Screen;
import com.afqa123.intergalactic.screens.TestScreen;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class IntergalacticGame extends ApplicationAdapter {
	
    private Screen screen;
    private Galaxy galaxy;
    
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

        loadAssets();

        galaxy = new Galaxy(15);

        //screen = new BloomTestScreen();
        screen = new TestScreen();
        //screen = new GalaxyScreen(galaxy);
        screen.activate();
    }

    private void loadAssets() {
        Gdx.app.log(IntergalacticGame.class.getName(), "Loading assets...");

        // Assets
        Assets.load("textures/nebula32.png", Texture.class);
        Assets.load("textures/base-red.png", Texture.class);
        Assets.load("textures/detail.png", Texture.class);
        Assets.load("textures/selection.png", Texture.class);        
        Assets.load("textures/explosion.png", Texture.class);        
        
        // Scene shaders
        Assets.load("shaders/sc_sphere.vsh", String.class);
        Assets.load("shaders/sc_sphere.fsh", String.class);
        Assets.load("shaders/sc_star.vsh", String.class);
        Assets.load("shaders/sc_star.fsh", String.class);
        Assets.load("shaders/sc_star_noise.vsh", String.class);
        Assets.load("shaders/sc_star_noise.fsh", String.class);

        Assets.load("shaders/default.vsh", String.class);
        Assets.load("shaders/default.fsh", String.class);
        Assets.load("shaders/color.vsh", String.class);
        Assets.load("shaders/color.fsh", String.class);
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
	}   

    @Override
    public void dispose() {
        screen.dispose();
        ShaderFactory.freeShaders();
    }
}