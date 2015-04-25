package com.afqa123.intergalactic.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

// Review this - I think having a global / static is the way to go, but
public class Assets {

    private static final AssetManager am = new AssetManager();
    
    static {
        // Register custom asset loaders
        am.setLoader(String.class, new StringAssetLoader(new InternalFileHandleResolver()));        
    }
 
    public static AssetManager getManager() {
        return am;
    }
    
    public static void load(String filename, Class claz) {
        am.load(filename, claz);
    }
    
    public static <T> T get(String filename) {
        return (T)am.get(filename);
    }
}