package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.graphics.SectorRenderer;
import static com.afqa123.intergalactic.screens.AbstractScreen.STAGE_MARGIN;
import com.afqa123.intergalactic.ui.ChangeListener;
import com.afqa123.intergalactic.ui.ProductionGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class SectorScreen extends AbstractScreen {
    
    private class SectorScreenInputProcessor extends InputAdapter {
        
        @Override
        public boolean keyDown(int i) {
            if (i == Input.Keys.ESCAPE) {
                getGame().popScreen();
                return true;
            } else {
                return false;
            }
        }
    }
    
    private final PerspectiveCamera cam;
    private final Sector sector;
    private final SectorRenderer renderer;
    
    // UI
    private final Label sectorLabel;
    private final Label productionLabel;
    private final TextButton backButton;
    private final DragAndDrop dnd;
    private final ProductionGroup foodProduction;
    private final ProductionGroup indProduction;
    private final ProductionGroup sciProduction;
        
    public SectorScreen(final IntergalacticGame game, final Sector sector) {        
        super(game);
        this.sector = sector;
        
        dnd = new DragAndDrop();
        
        // Create ui components
        sectorLabel = new Label(null, getSkin(), FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
        sectorLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        sectorLabel.setPosition(STAGE_MARGIN, 50.0f);
        getStage().addActor(sectorLabel);
        
        productionLabel = new Label(null, getSkin(), FONT, new Color(0.0f, 1.0f, 0.0f, 1.0f));
        productionLabel.setPosition(STAGE_WIDTH / 2 - 75, 70.0f);        
        getStage().addActor(productionLabel);
        
        backButton = new TextButton(getGame().getLabels().getProperty("BUTTON_BACK"), getSkin());
        backButton.setPosition(STAGE_MARGIN, STAGE_HEIGHT - STAGE_MARGIN - backButton.getHeight());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().popScreen();
            }
        });
        getStage().addActor(backButton);
        
        Texture texture = Assets.get("textures/ui.png");
        TextureRegion tr = new TextureRegion(texture, 0.125f, 0.0f, 0.15625f, 0.03125f);

        foodProduction = new ProductionGroup(dnd, tr, sector.getFoodProducers());
        foodProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN + 2 * tr.getRegionHeight());
        foodProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                sector.setFoodProducers(value);
                sector.computerModifiers();
                // TODO: replace with ModelChangedListener...
                updateLabels();
            }            
        });
        getStage().addActor(foodProduction);
        
        indProduction = new ProductionGroup(dnd, tr, sector.getIndustrialProducers());
        indProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN + tr.getRegionHeight());
        indProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                sector.setIndustrialProducers(value);
                sector.computerModifiers();
                updateLabels();
            }            
        });
        getStage().addActor(indProduction);

        sciProduction = new ProductionGroup(dnd, tr, sector.getScienceProducers());
        sciProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN);
        sciProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                sector.setScienceProducers(value);
                sector.computerModifiers();
                updateLabels();
            }            
        });
        getStage().addActor(sciProduction);  
        
        cam = new PerspectiveCamera(42.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0.0f, 0.0f, 4.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 0.5f;
        cam.far = 100.0f;
        cam.update();
        
        renderer = new SectorRenderer(sector);        
    }
    
    @Override
    public void activate() {
        super.activate();
 
        // Update viewport in case it changed
        cam.viewportWidth = Gdx.graphics.getWidth();
        cam.viewportHeight = Gdx.graphics.getHeight();
        cam.update();

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(Gdx.input.getInputProcessor());
        im.addProcessor(new SectorScreenInputProcessor());
        Gdx.input.setInputProcessor(im);
        
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

        renderer.render(cam);            

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
        String info = String.format("%s\nPopulation: %d / %d\nGrowth: %.2f %%\nMorale: %s", 
                sector.getName(), (int)sector.getPopulation(), sector.getMaxPopulation(), 
                sector.getGrowthRate(), sector.getMorale().getLabel());                
        sectorLabel.setText(info);
                
        String prod = String.format("\nFood %.2f\nInd %.2f\nSci %.2f",
                sector.getNetFoodOutput(), sector.getIndustrialOutput(), sector.getScientificOutput());
        productionLabel.setText(prod);
    }
}