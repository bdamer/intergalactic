package com.afqa123.intergalactic.asset;

import com.afqa123.intergalactic.asset.PropertiesAssetLoader.PropertiesParameter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple {@code AssetLoader} to read a {@code Properties} collection from an asset.
 */
public class PropertiesAssetLoader extends AsynchronousAssetLoader<Properties, PropertiesParameter> {

    public static class PropertiesParameter extends AssetLoaderParameters<Properties> { }
    
    private Properties props;
    
    public PropertiesAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager am, String fileName, FileHandle fh, PropertiesParameter p) {
        this.props = new Properties();
        try {
            props.load(fh.read());
        } catch (IOException ex) {
            Gdx.app.error(PropertiesAssetLoader.class.getName(), "Could not load properties.", ex);
            props = null;
        }
    }

    @Override
    public Properties loadSync(AssetManager am, String fileName, FileHandle fh, PropertiesParameter p) {
        Properties pr = this.props;
        this.props = null;
        return pr;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle fh, PropertiesParameter p) {
        return null;
    }
}