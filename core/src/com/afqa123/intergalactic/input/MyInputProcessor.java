package com.afqa123.intergalactic.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {

    @Override
    public boolean keyDown(int i) {
        if (i == Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        } else {
            return false;
        }
    }
}