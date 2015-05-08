package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.graphics.StarRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.List;

public class SectorScreen extends AbstractScreen {

    private class SectorScreenInputProcessor extends InputAdapter {
        
        @Override
        public boolean keyDown(int i) {
            if (i == Input.Keys.ESCAPE) {
                setDone(true);
                return true;
            } else {
                return false;
            }
        }
    }
    
    private final PerspectiveCamera cam;
    private Sector sector;
    private StarRenderer renderer;
    
    // UI
    private final Label sectorLabel;
    private final Label productionLabel;
    private final TextButton backButton;
        
    public SectorScreen(IntergalacticGame game, Sector sector) {        
        super(game);
        this.sector = sector;
        
        // Create ui components
        sectorLabel = new Label(null, getSkin(), FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
        sectorLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        sectorLabel.setPosition(STAGE_MARGIN, 50.0f);
        getStage().addActor(sectorLabel);
        
        productionLabel = new Label(null, getSkin(), FONT, new Color(0.0f, 1.0f, 0.0f, 1.0f));
        productionLabel.setPosition(STAGE_WIDTH / 2, 50.0f);        
        getStage().addActor(productionLabel);
        
        backButton = new TextButton("Back", getSkin());
        backButton.setPosition(STAGE_MARGIN, STAGE_HEIGHT - 4 * STAGE_MARGIN);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().popScreen();
            }
        });
        getStage().addActor(backButton);
        
        cam = new PerspectiveCamera(45.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 10.0f, 5.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        renderer = new StarRenderer();        
    }
    
    @Override
    public void activate() {
        super.activate();
 
        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();
        
        //Gdx.input.setInputProcessor(new SectorScreenInputProcessor());        

        updateLabels();
    }

    @Override
    public void deactivate() {
    
    }

    @Override
    public void update() {
        cam.update();
        getStage().act();
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
        getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    @Override
    public void dispose() {
        super.dispose();
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