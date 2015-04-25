package com.afqa123.intergalactic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.screens.GalaxyScreen;
import com.afqa123.intergalactic.screens.Screen;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class IntergalacticGame extends ApplicationAdapter {
	
    private Screen screen;
    private Galaxy galaxy;
    
	@Override
	public void create () {
        // Application settings
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // OpenGL settings
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        loadAssets();

        galaxy = new Galaxy(10);
        screen = new GalaxyScreen(galaxy);
        screen.activate();
    }

    private void loadAssets() {
        Gdx.app.log(IntergalacticGame.class.getName(), "Loading assets...");
        Assets.load("shaders/default.vsh", String.class);
        Assets.load("shaders/default.fsh", String.class);
        Assets.load("shaders/color.vsh", String.class);
        Assets.load("shaders/color.fsh", String.class);
        Assets.getManager().finishLoading();        
    }
    
    @Override
    public void resize(int width, int height) {
        Gdx.app.debug(IntergalacticGame.class.getName(), String.format("Resizing screen to %d by %d", width, height));
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
    }    
}