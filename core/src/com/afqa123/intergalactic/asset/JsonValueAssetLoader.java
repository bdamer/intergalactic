package com.afqa123.intergalactic.asset;

import com.afqa123.intergalactic.asset.JsonValueAssetLoader.JsonValueParameter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JsonValueAssetLoader extends AsynchronousAssetLoader<JsonValue, JsonValueParameter> {

   public static class JsonValueParameter extends AssetLoaderParameters<JsonValue> { }
    
    private String string;
    
    public JsonValueAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager am, String fileName, FileHandle fh, JsonValueParameter p) {
        this.string = fh.readString();
    }

    @Override
    public JsonValue loadSync(AssetManager am, String fileName, FileHandle fh, JsonValueParameter p) {
        JsonValue root = new JsonReader().parse(this.string);
        this.string = null;
        return root;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle fh, JsonValueParameter p) {
        return null;
    }
}