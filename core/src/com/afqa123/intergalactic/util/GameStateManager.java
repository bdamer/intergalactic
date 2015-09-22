package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.IntergalacticGame;
import com.afqa123.intergalactic.model.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameStateManager {

    private static final String PREFIX_AUTO = "autosave";
    private static final String EXTENSION = "sav";
    private static final int MAX_INDEX = 100;
    private final File baseDir;
    
    public GameStateManager(String directory) {
        baseDir = new File(directory);
    }
    
    public GameStateManager(File directory) {
        baseDir = directory;
    }
    
    public void saveSession(String prefix, Session session, boolean rollExisting) {
        File file = new File(baseDir, String.format("%s.%s", prefix, EXTENSION));
        Gdx.app.log(GameStateManager.class.getName(), "Saving session to: " + file);
        try {
            if (rollExisting) {
                rollFile(file);
            }
            try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                writer.setOutputType(JsonWriter.OutputType.json);
                Json json = new Json();
                json.toJson(session, writer);            
            }
        } catch (IOException ex) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error saving file: " + file, ex);
        }
    }
    
    public Session loadSession(String filename) {
        File file = new File(baseDir, filename);
        Gdx.app.log(GameStateManager.class.getName(), "Loading session from: " + file);
        try (FileReader reader = new FileReader(file)) {
            Json json = new Json();
            return json.fromJson(Session.class, reader);
        } catch (IOException ex) {
            Gdx.app.error(IntergalacticGame.class.getName(), "Error loading file: " + file.getName(), ex);
            return null;
        }
    }

    public void saveAuto(Session session) {
        // Save twice - once as "autosave.sav" and once as "autosave-<turn>.sav"
        saveSession(PREFIX_AUTO, session, false);
        saveSession(String.format("%s_%d", PREFIX_AUTO, session.getTurn()), session, false);
    }
    
    public boolean hasAutoSave() {
        File file = new File(baseDir, String.format("%s.%s", PREFIX_AUTO, EXTENSION));
        return file.exists();
    }
    
    public Session loadAuto() {
        return loadSession(String.format("%s.%s", PREFIX_AUTO, EXTENSION));
    }
    
    private void rollFile(File file) throws IOException {
        if (file.exists()) {
            int pos = file.getName().lastIndexOf(".");
            String ext = file.getName().substring(pos + 1);
            String basename;
            int index;
            try {
                index = Integer.parseInt(ext);
                basename = file.getName().substring(0, pos);
            } catch (NumberFormatException e) {
                index = 0;
                basename = file.getName();
            }
            index++;
            if (index > MAX_INDEX) {
                file.delete();
            } else {
                Path oldPath = FileSystems.getDefault().getPath(file.getParent(), file.getName());
                Path newPath = FileSystems.getDefault().getPath(file.getParent(), String.format("%s.%d", basename, index));
                rollFile(newPath.toFile());
                Files.move(oldPath, newPath);
            }
        }        
    }
}
