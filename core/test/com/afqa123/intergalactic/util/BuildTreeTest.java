package com.afqa123.intergalactic.util;

import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.TestAssetManager;
import com.afqa123.intergalactic.data.Sector;
import com.afqa123.intergalactic.data.Sector.StarCategory;
import com.afqa123.intergalactic.data.Structure;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.utils.JsonValue;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BuildTreeTest {

    @BeforeClass
    public static void beforeClass() {
        Assets.setAssetManager(new TestAssetManager());
        Assets.load("data/ships.json", JsonValue.class);
        Assets.load("data/structures.json", JsonValue.class);
    }
    
    @Test
    public void testGetBuildOptions() {
        Sector sector = new Sector("Sol", new HexCoordinate(0,0), StarCategory.YELLOW);
        BuildTree bt = new BuildTree();
        List<Structure> actual = bt.getBuildOptions(sector);
        Assert.assertEquals(5, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("barracks")));
        sector.getStructures().add(bt.getStructure("barracks"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(4, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("climate_control")));
        sector.getStructures().add(bt.getStructure("climate_control"));
                
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("fabricators")));
        sector.getStructures().add(bt.getStructure("fabricators"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("shipyard")));
        sector.getStructures().add(bt.getStructure("shipyard"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("offworld_farming")));
        sector.getStructures().add(bt.getStructure("offworld_farming"));        
        
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("research_lab")));
        sector.getStructures().add(bt.getStructure("research_lab"));
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("mass_replication")));
        sector.getStructures().add(bt.getStructure("mass_replication"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(3, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("nano_fabrication")));
        sector.getStructures().add(bt.getStructure("nano_fabrication"));
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("scanner")));
        sector.getStructures().add(bt.getStructure("scanner"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("science_academy")));
        sector.getStructures().add(bt.getStructure("science_academy"));
    
        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(2, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("shield_generator")));
        sector.getStructures().add(bt.getStructure("shield_generator"));

        actual = bt.getBuildOptions(sector);
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.contains(bt.getStructure("orbital_laser")));
        sector.getStructures().add(bt.getStructure("orbital_laser"));
    }
    
    @Test
    public void testCanBuild() {
        Sector sector = new Sector("Sol", new HexCoordinate(0,0), StarCategory.YELLOW);
        BuildTree bt = new BuildTree();
        Assert.assertTrue(bt.canBuild(sector, bt.getStructure("barracks")));
        Assert.assertFalse(bt.canBuild(sector, bt.getStructure("mass_replication")));        
    }
}