package com.afqa123.intergalactic.data;

import com.badlogic.gdx.math.Vector2;
import org.junit.Assert;
import org.junit.Test;

public class GalaxyTest {

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
        Sector s = g.getSector(0, 0);
        Assert.assertNotNull(s);
        Assert.assertEquals(0, s.getX());
        Assert.assertEquals(0, s.getY());
        
        g = new Galaxy(2);
        s = g.getSector(0, 0);
        Assert.assertNotNull(s);
        Assert.assertEquals(0, s.getX());
        Assert.assertEquals(0, s.getY());

        s = g.getSector(0, -1);
        Assert.assertNotNull(s);        
        Assert.assertEquals(0, s.getX());
        Assert.assertEquals(-1, s.getY());
        
        s = g.getSector(0, 1);
        Assert.assertNotNull(s);        
        Assert.assertEquals(0, s.getX());
        Assert.assertEquals(1, s.getY());
        
        s = g.getSector(1, -1);
        Assert.assertNotNull(s);        
        Assert.assertEquals(1, s.getX());
        Assert.assertEquals(-1, s.getY());
    }
}