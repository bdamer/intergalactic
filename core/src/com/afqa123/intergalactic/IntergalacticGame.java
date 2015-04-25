package com.afqa123.intergalactic;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.data.Galaxy;
import com.afqa123.intergalactic.graphics.GridRenderer;
import com.afqa123.intergalactic.input.MyInputProcessor;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;

public class IntergalacticGame extends ApplicationAdapter {
	
    private PerspectiveCamera cam;
    private CameraInputController camController;
    
    private Environment environment;
    
    private GridRenderer grid;
    private Galaxy galaxy;
    
    ImmediateModeRenderer20 r;
    
    
	@Override
	public void create () {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        
        // Load assets
        Assets.load("shaders/default.vsh", String.class);
        Assets.load("shaders/default.fsh", String.class);
        Assets.load("shaders/color.vsh", String.class);
        Assets.load("shaders/color.fsh", String.class);
        Assets.getManager().finishLoading();
        
	    cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.fieldOfView = 70;
        cam.position.set(0.0f, 2.5f, 5.0f);
        cam.lookAt(0.0f, 0.0f, 0.0f);
        cam.near = 1.0f;
        cam.far = 500.0f;
        cam.update();
        
        camController = new CameraInputController(cam);

        InputMultiplexer mp = new InputMultiplexer();
        mp.addProcessor(camController);
        mp.addProcessor(new MyInputProcessor());
        Gdx.input.setInputProcessor(mp);

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        
        //environment = new Environment();
        //environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        
        galaxy = new Galaxy(10);
        grid = new GridRenderer(galaxy);
        grid.update();	
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug(IntergalacticGame.class.getName(), String.format("Resizing screen to %d by %d", width, height));
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }
    
	@Override
	public void render () {
        camController.update();
        
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        /*r = new ImmediateModeRenderer20(false, true, 0);
        r.begin(cam.combined, GL20.GL_TRIANGLES);        
        r.color(1.0f, 0.0f, 0.0f, 0.0f);
        r.vertex(0.0f, 0.0f, 0.0f);
        r.color(1.0f, 0.0f, 0.0f, 0.0f);
        r.vertex(1.0f, 1.0f, 0.0f);
        r.color(1.0f, 0.0f, 0.0f, 0.0f);
        r.vertex(-1.0f, 1.0f, 0.0f);
        r.end();*/
        
        grid.render(cam);        
	}   

    @Override
    public void dispose() {
        grid.dispose();
    }    
}