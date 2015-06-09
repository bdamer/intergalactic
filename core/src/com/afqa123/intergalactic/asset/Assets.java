package com.afqa123.intergalactic.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Properties;

// Review this - I think having a global / static is the way to go
public class Assets {

    private static AssetManager am;

    static {
        // Register custom asset loaders
        am = new AssetManager();
        am.setLoader(JsonValue.class, new JsonValueAssetLoader(new InternalFileHandleResolver()));
        am.setLoader(Properties.class, new PropertiesAssetLoader(new InternalFileHandleResolver()));
        am.setLoader(String.class, new StringAssetLoader(new InternalFileHandleResolver()));
    }
    
    public static void setAssetManager(AssetManager am) {
        Assets.am = am;
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