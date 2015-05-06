package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.asset.FontProvider;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.List;

public class SectorScreen implements Screen {

    private static final int WIDTH = 960;
    private static final int HEIGHT = 540;
    private static final int MARGIN = 10;
    private static final String FONT = "scaled-font";
    
    private class SectorScreenInputProcessor extends InputAdapter {
        
        @Override
        public boolean keyDown(int i) {
            if (i == Input.Keys.ESCAPE) {
                done = true;
                return true;
            } else {
                return false;
            }
        }
    }
    
    private final PerspectiveCamera cam;
    private Sector sector;
    private StarRenderer renderer;
    private boolean done;
    
    // UI
    private final Stage stage;
    private final Skin skin;    
    private final Label sectorLabel;
    private final Label productionLabel;
        
    public SectorScreen() {        
        // TODO: look into viewports for different ratios
        stage = new Stage(new FitViewport(WIDTH, HEIGHT));
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add(FONT, FontProvider.getFont());
                
        // Create ui components
        sectorLabel = new Label(null, skin, FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
        sectorLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        sectorLabel.setPosition(MARGIN, 50.0f);
        stage.addActor(sectorLabel);
        
        productionLabel = new Label(null, skin, FONT, new Color(0.0f, 1.0f, 0.0f, 1.0f));
        productionLabel.setPosition(WIDTH / 2, 50.0f);        
        stage.addActor(productionLabel);
        
        cam = new PerspectiveCamera(45.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 10.0f, 5.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        sector = new Sector("Kronos", new HexCoordinate(0,0), Sector.StarCategory.RED);        
        renderer = new StarRenderer();        
    }
    
    @Override
    public void activate() {
        done = false;
 
        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        //Gdx.input.setInputProcessor(new SectorScreenInputProcessor());        
        Gdx.input.setInputProcessor(stage);

        updateLabels();
    }

    @Override
    public void deactivate() {
    
    }

    @Override
    public void update() {
        cam.update();
        stage.act();
    }
    
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        // Scene pass
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // TODO: clean up - this should use a new renderer to show the star
        // and planets
        List<Sector> s = new ArrayList<>();
        s.add(sector);
        renderer.render(cam, s);            

        // UI pass
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
        
        stage.getViewport().update(width, height, true);        
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void updateLabels() {
        // update UI from sector
        String info = String.format("%s\nPopulation: %d / %d\nGrowth: %.1f %%\nMorale: %s", 
                sector.getName(), (int)sector.getPopulation(), sector.getMaxPopulation(), 
                sector.getGrowthRate(), sector.getMorale().getLabel());                
        sectorLabel.setText(info);
        String prod = String.format("\nFood %d  +%d\nInd %d  +%d\nSci %d  +%d",
                sector.getFoodProducers(), sector.getFoodProducers(),
                sector.getIndustrialProducers(), sector.getIndustrialProducers(),
                sector.getScienceProducers(), sector.getScienceProducers());
        productionLabel.setText(prod);
    }
}