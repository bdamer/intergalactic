package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.logic.BuildTree;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.TestAssetManager;
import com.afqa123.intergalactic.logic.EntityDatabase;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.BuildOption;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.StarType;
import com.badlogic.gdx.utils.JsonValue;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BuildTreeTest {

    private static EntityDatabase db;
    
    @BeforeClass
    public static void beforeClass() {
        Assets.setAssetManager(new TestAssetManager());
        Assets.load("data/ships.json", JsonValue.class);
        Assets.load("data/structures.json", JsonValue.class);
        db = new EntityDatabase();
    }
    
    @Test
    public void testGetAvailableStructures() {
        Sector sector = new Sector("Sol", new HexCoordinate(0,0), StarType.YELLOW);
        BuildTree bt = new BuildTree(db);
        List<BuildOption> actual = bt.getBuildOptions(sector);
        Assert.assertEquals(5, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("barracks")));
        sector.getStructures().add("barracks");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(4, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("climate_control")));
        sector.getStructures().add("climate_control");
                
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("fabricators")));
        sector.getStructures().add("fabricators");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("shipyard")));
        sector.getStructures().add("shipyard");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("offworld_farming")));
        sector.getStructures().add("offworld_farming");        
        
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("research_lab")));
        sector.getStructures().add("research_lab");
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("mass_replication")));
        sector.getStructures().add("mass_replication");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("nano_fabrication")));
        sector.getStructures().add("nano_fabrication");
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("scanner")));
        sector.getStructures().add("scanner");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("science_academy")));
        sector.getStructures().add("science_academy");
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("shield_generator")));
        sector.getStructures().add("shield_generator");

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(db.getStructure("orbital_laser")));
        sector.getStructures().add("orbital_laser");
    }
    
    @Test
    public void testCanBuild() {
        Sector sector = new Sector("Sol", new HexCoordinate(0,0), StarType.YELLOW);
        BuildTree bt = new BuildTree(db);
        Assert.assertTrue(bt.canBuild(sector, db.getStructure("barracks")));
        Assert.assertFalse(bt.canBuild(sector, db.getStructure("mass_replication")));        
    }
}