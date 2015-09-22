package com.afqa123.intergalactic.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TestAssetManager extends AssetManager {

    private static final Map<String,Class> TYPES = new HashMap<>();
    private static final File ASSET_DIR = new File("../android/assets/");
    
    @Override
    public <T> void load(String fileName, Class<T> type) {
        TYPES.put(fileName, type);
    }
    
    @Override
    public <T extends Object> T get(String fileName) {
        Class<T> type = TYPES.get(fileName);        
        try {
            File f = new File(ASSET_DIR, fileName);
            if (type.equals(String.class)) {
                Path path = FileSystems.getDefault().getPath(f.getAbsolutePath());
                return (T)new String(Files.readAllBytes(path));        
            } else if (type.equals(JsonValue.class)) {
                Path path = FileSystems.getDefault().getPath(f.getAbsolutePath());
                String raw = new String(Files.readAllBytes(path));        
                return (T)new JsonReader().parse(raw);
            } else {
                throw new RuntimeException("Could not load " + fileName);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error loading asset: " + fileName, ex);
        }       
    }
}