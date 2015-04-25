package com.afqa123.intergalactic.asset;

import com.afqa123.intergalactic.asset.StringAssetLoader.StringParameter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Simple {@code AssetLoader} to read a {@code String} from an asset.
 */
public class StringAssetLoader extends AsynchronousAssetLoader<String, StringParameter> {

    public static class StringParameter extends AssetLoaderParameters<String> { }
    
    private String string;
    
    public StringAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager am, String fileName, FileHandle fh, StringParameter p) {
        this.string = fh.readString();
    }

    @Override
    public String loadSync(AssetManager am, String fileName, FileHandle fh, StringParameter p) {
        String s = this.string;
        this.string = null;
        return s;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle fh, StringParameter p) {
        return null;
    }
}