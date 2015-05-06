package com.afqa123.intergalactic.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

// TODO: integrate with resource / asset mgmt
public class FontProvider {

    private static FreeTypeFontGenerator gen;
    private static BitmapFont font;
    
    public static void intialize() {
        FileHandle fh = Gdx.files.internal("fonts/ostrich-sans/ostrich-black.ttf");
        gen = new FreeTypeFontGenerator(fh);

        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        
        font = gen.generateFont(parameter);
    }
  
    public static void free() {
        gen.dispose();
    }
    
    public static BitmapFont getFont() {
        return font;
    }
}