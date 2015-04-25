package com.afqa123.intergalactic.screens;

import com.badlogic.gdx.utils.Disposable;

public interface Screen extends Disposable {

    void activate();
    
    void deactivate();
    
    void update();
    
    void render();
    
    void resize(int width, int height);
    
    boolean isDone();    
}
