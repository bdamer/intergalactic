package com.afqa123.intergalactic.screens;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.model.BuildQueueEntry;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.graphics.SectorRenderer;
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
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
    };
    
    private final PerspectiveCamera cam;
    private final Sector sector;
    private final SectorRenderer renderer;
    
    // UI
    private final Label sectorLabel;
    private final Label productionLabel;
    private final TextButton backButton;
    private final Label structuresLabel;
    private final TextButton buildQueueButton;
    private final SelectBox buildQueueSelect;
    private final Label buildQueueLabel;
    private final DragAndDrop dnd;
    private final ProductionGroup foodProduction;
    private final ProductionGroup indProduction;
    private final ProductionGroup sciProduction;
        
    public SectorScreen(IntergalacticGame game, Sector sector) {        
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

        structuresLabel = new Label(null, getSkin(), FONT, new Color(0.0f, 1.0f, 0.0f, 1.0f));
        structuresLabel.setPosition(STAGE_MARGIN, backButton.getY() - backButton.getHeight() - STAGE_MARGIN);
        getStage().addActor(structuresLabel);
        
        buildQueueButton = new TextButton(getGame().getLabels().getProperty("BUTTON_ADD"), getSkin());
        buildQueueButton.setPosition(STAGE_WIDTH - STAGE_MARGIN - buildQueueButton.getWidth(), 
                STAGE_HEIGHT - STAGE_MARGIN - buildQueueButton.getHeight());
        buildQueueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArraySelection s = buildQueueSelect.getSelection();
                if (!s.isEmpty()) {
                    SectorScreen.this.sector.getBuildQueue().add((BuildQueueEntry)s.getLastSelected());
                    updateControls();
                }
            }
        });
        getStage().addActor(buildQueueButton);
        
        buildQueueSelect = new SelectBox<>(getSkin());
        buildQueueSelect.setWidth(200.0f);
        buildQueueSelect.setPosition(buildQueueButton.getX() - buildQueueSelect.getWidth(), 
                STAGE_HEIGHT - STAGE_MARGIN - buildQueueSelect.getHeight());
        getStage().addActor(buildQueueSelect);

        buildQueueLabel = new Label(null, getSkin(), FONT, new Color(1.0f, 1.0f, 1.0f, 1.0f));
        buildQueueLabel.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buildQueueLabel.setPosition(buildQueueSelect.getX(), buildQueueSelect.getY());
        getStage().addActor(buildQueueLabel);        
        
        Texture texture = Assets.get("textures/ui.png");
        TextureRegion tr = new TextureRegion(texture, 0.125f, 0.0f, 0.15625f, 0.03125f);

        foodProduction = new ProductionGroup(dnd, tr, sector.getFoodProducers());
        foodProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN + 2 * tr.getRegionHeight());
        foodProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                SectorScreen.this.sector.setFoodProducers(value);
                SectorScreen.this.sector.updateModifiers();
                // TODO: replace with ModelChangedListener...
                updateControls();
            }            
        });
        getStage().addActor(foodProduction);
        
        indProduction = new ProductionGroup(dnd, tr, sector.getIndustrialProducers());
        indProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN + tr.getRegionHeight());
        indProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                SectorScreen.this.sector.setIndustrialProducers(value);
                SectorScreen.this.sector.updateModifiers();
                updateControls();
            }            
        });
        getStage().addActor(indProduction);

        sciProduction = new ProductionGroup(dnd, tr, sector.getScienceProducers());
        sciProduction.setPosition(STAGE_WIDTH / 2, STAGE_MARGIN);
        sciProduction.setChangeListener(new ChangeListener<Integer>() {
            @Override
            public void valueChanged(Integer value) {
                SectorScreen.this.sector.setScienceProducers(value);
                SectorScreen.this.sector.updateModifiers();
                updateControls();
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
        
        updateControls();
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

    private void updateControls() {
        // update UI from sector
        StringBuilder sb = new StringBuilder();
        sb.append(sector.getName());
        sb.append("\nPopulation: ");
        sb.append((int)sector.getPopulation());
        sb.append(" / ");
        sb.append(sector.getMaxPopulation());
        sb.append("\n");        
        if (sector.getGrowthRate() >= 0.0f) {
            sb.append("Growth in ");
            sb.append(sector.getTurnsUntilGrowth());
            sb.append(" Turn(s)");
        } else {
            sb.append("Food shortage!");
        }
        sb.append(String.format(" (%.2f %%)", sector.getGrowthRate()));
        sb.append("\nMorale: ");
        sb.append(sector.getMorale().getLabel());
        sectorLabel.setText(sb.toString());
                
        String prod = String.format("\nFood %.2f\nInd %.2f\nSci %.2f",
                sector.getNetFoodOutput(), sector.getIndustrialOutput(), sector.getScientificOutput());
        productionLabel.setText(prod);
        
        // Populate list of existing structure
        sb.setLength(0);
        for (String id : sector.getStructures()) {
            sb.append(getState().getDatabase().getStructure(id).getLabel());
            sb.append("\n");
        }
        structuresLabel.setText(sb.toString());
        
        // Populate build queue list
        Set<String> inQueue = new HashSet<>();
        Queue<BuildQueueEntry> queue = sector.getBuildQueue();
        sb.setLength(0);
        float height = 0;
        for (BuildQueueEntry e : queue) {
            inQueue.add(e.getId());
            sb.append(e.getLabel());
            sb.append(" (");
            if (sector.getIndustrialOutput() > 0.0f) {
                sb.append(Math.round(e.getCost() / sector.getIndustrialOutput()));
            } else {
                sb.append(-1);
            }            
            sb.append(")\n");
            height += 10.0f; // TODO: this is not accurate
        }
        buildQueueLabel.setText(sb);
        buildQueueLabel.setPosition(buildQueueSelect.getX(), 
            buildQueueSelect.getY() - buildQueueSelect.getHeight() - height);
        
        // Populate build option dropdown
        List<BuildOption> availableStructures = getState().getBuildTree().getBuildOptions(sector);
        List<BuildQueueEntry> buildOptionLabels = new ArrayList<>();
        for (BuildOption option : availableStructures) {
            if (option.isUnique() && inQueue.contains(option.getId())) {
                continue;
            }            
            buildOptionLabels.add(new BuildQueueEntry(option.getId(), option.getLabel(), option.getCost()));
        }
        
        buildQueueSelect.setItems(buildOptionLabels.toArray());
    }
}