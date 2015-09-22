package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.TestApplication;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.TestAssetManager;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Session;
import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameStateManagerTest {

    private Session session;
    private Path tmpDir;
    
    @BeforeClass
    public static void beforeClass() {
        Gdx.app = new TestApplication();
        Assets.setAssetManager(new TestAssetManager());
        Assets.load("data/structures.json", String.class);
        Assets.load("data/ships.json", String.class);
        Assets.load("data/stations.json", String.class);
    }

    @Before
    public void before() throws IOException {
        session = new Session(new Galaxy(3), new HashMap<String, Faction>());
        tmpDir = Files.createTempDirectory("gamestate");
    }
    
    @After
    public void after() throws IOException {
        File tmp = tmpDir.toFile();
        if (tmp.exists()) {
            tmp.delete();
        }
    }
    
    @Test
    public void testSaveAndLoad() {
        GameStateManager mgr = new GameStateManager(tmpDir.toFile());
        mgr.saveSession("test", session, false);
        Session actual = mgr.loadSession("test.sav");
        Assert.assertNotNull(actual);
        Assert.assertEquals(session.getTurn(), actual.getTurn());
    }
    
    @Test
    public void testRollFile() {
        GameStateManager mgr = new GameStateManager(tmpDir.toFile());
        mgr.saveSession("test", session, true);
        File saveFile = new File(tmpDir.toFile(), "test.sav");
        Assert.assertTrue(saveFile.exists());
        
        mgr.saveSession("test", session, true);
        Assert.assertTrue(saveFile.exists());
        File saveFile1 = new File(tmpDir.toFile(), "test.sav.1");
        Assert.assertTrue(saveFile1.exists());
        
        mgr.saveSession("test", session, true);
        Assert.assertTrue(saveFile.exists());
        saveFile1 = new File(tmpDir.toFile(), "test.sav.1");
        Assert.assertTrue(saveFile1.exists());
        File saveFile2 = new File(tmpDir.toFile(), "test.sav.2");
        Assert.assertTrue(saveFile2.exists());
    }
    
    @Test
    public void testAutosave() {
        GameStateManager mgr = new GameStateManager(tmpDir.toFile());
        mgr.saveAuto(session);
        File saveFile = new File(tmpDir.toFile(), "autosave_0.sav");
        Assert.assertTrue(saveFile.exists());
        saveFile = new File(tmpDir.toFile(), "autosave.sav");
        Assert.assertTrue(saveFile.exists());

        session.increaseTurns();
        mgr.saveAuto(session);
        saveFile = new File(tmpDir.toFile(), "autosave_1.sav");
        Assert.assertTrue(saveFile.exists());
        saveFile = new File(tmpDir.toFile(), "autosave.sav");
        Assert.assertTrue(saveFile.exists());
        
        session.increaseTurns();
        mgr.saveAuto(session);
        saveFile = new File(tmpDir.toFile(), "autosave_2.sav");
        Assert.assertTrue(saveFile.exists());
        saveFile = new File(tmpDir.toFile(), "autosave.sav");
        Assert.assertTrue(saveFile.exists());
    }   
}