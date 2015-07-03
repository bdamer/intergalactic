package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.TestApplication;
import com.afqa123.intergalactic.asset.Assets;
import com.afqa123.intergalactic.asset.TestAssetManager;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GalaxyTest {

    @BeforeClass
    public static void beforeClass() {
        Gdx.app = new TestApplication();
        Assets.setAssetManager(new TestAssetManager());
        Assets.load("data/sectors.json", JsonValue.class);
    }
    
    @Test
    public void testCreate() {
        Galaxy g = new Galaxy(1);
        Assert.assertEquals(1, g.getCount());
        g = new Galaxy(2);
        Assert.assertEquals(7, g.getCount());
        g = new Galaxy(10);
        Assert.assertEquals(271, g.getCount());
    }

    @Test
    public void testAxialToOffset() {
        Galaxy g = new Galaxy(2);
        Assert.assertEquals(new Vector2(0, 0), g.axialToOffset(new Vector2(0, -1)));
        Assert.assertEquals(new Vector2(1, 0), g.axialToOffset(new Vector2(1, -1)));
        Assert.assertEquals(new Vector2(0, 1), g.axialToOffset(new Vector2(-1, 0)));
        Assert.assertEquals(new Vector2(1, 1), g.axialToOffset(new Vector2(0, 0)));
        Assert.assertEquals(new Vector2(2, 1), g.axialToOffset(new Vector2(1, 0)));
        Assert.assertEquals(new Vector2(0, 2), g.axialToOffset(new Vector2(-1, 1)));
        Assert.assertEquals(new Vector2(1, 2), g.axialToOffset(new Vector2(0, 1)));        
    }

    @Test
    public void testOffsetToAxial() {
        Galaxy g = new Galaxy(2);
        Assert.assertEquals(new Vector2(0, -1), g.offsetToAxial(new Vector2(0, 0)));
        Assert.assertEquals(new Vector2(1, -1), g.offsetToAxial(new Vector2(1, 0)));
        Assert.assertEquals(new Vector2(-1, 0), g.offsetToAxial(new Vector2(0, 1)));
        Assert.assertEquals(new Vector2(0, 0), g.offsetToAxial(new Vector2(1, 1)));
        Assert.assertEquals(new Vector2(1, 0), g.offsetToAxial(new Vector2(2, 1)));
        Assert.assertEquals(new Vector2(-1, 1), g.offsetToAxial(new Vector2(0, 2)));
        Assert.assertEquals(new Vector2(0, 1), g.offsetToAxial(new Vector2(1, 2)));        
    }
    
    @Test
    public void testGetSector() {
        Galaxy g = new Galaxy(1);
        HexCoordinate c = new HexCoordinate(0, 0);
        Sector s = g.getSector(c);
        Assert.assertNotNull(s);
        Assert.assertEquals(c, s.getCoordinates());
        
        g = new Galaxy(2);
        s = g.getSector(c);
        Assert.assertNotNull(s);
        Assert.assertEquals(c, s.getCoordinates());

        c = new HexCoordinate(0, -1);
        s = g.getSector(c);
        Assert.assertNotNull(s);        
        Assert.assertEquals(c, s.getCoordinates());
        
        c = new HexCoordinate(0, 1);
        s = g.getSector(c);
        Assert.assertNotNull(s);        
        Assert.assertEquals(c, s.getCoordinates());
        
        c = new HexCoordinate(1, -1);
        s = g.getSector(c);
        Assert.assertNotNull(s);        
        Assert.assertEquals(c, s.getCoordinates());
    }
}