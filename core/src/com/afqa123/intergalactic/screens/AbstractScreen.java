package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.asset.FontProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Provides basic infrastructure for screens.
 */
public abstract class AbstractScreen implements Screen {
    
    protected static final int STAGE_WIDTH = 960;
    protected static final int STAGE_HEIGHT = 540;
    protected static final int STAGE_MARGIN = 10;
    protected static final String FONT = "scaled-font";

    private final Stage stage;
    private final Skin skin;    
    private final IntergalacticGame game;
    private boolean done;
    
    public AbstractScreen(IntergalacticGame game) {
        this.game = game;
        // TODO: look into viewports for different ratios
        stage = new Stage(new FitViewport(STAGE_WIDTH, STAGE_HEIGHT));
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add(FONT, FontProvider.getFont());
    }
    
    protected Stage getStage() {
        return stage;
    }
    
    protected Skin getSkin() {
        return skin;
    }
    
    protected IntergalacticGame getGame() {
        return game;
    }

    @Override
    public void activate() {
        done = false;
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public boolean isDone() {
        return done;
    }
    
    protected void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}