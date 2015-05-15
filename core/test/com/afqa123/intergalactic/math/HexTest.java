package com.afqa123.intergalactic.math;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HexTest {

    @Test
    public void testAxialToWorld() {
        Vector3 actual = Hex.axialToWorld(Vector2.Zero);
        Assert.assertEquals(new Vector3(0, 0, 0), actual);
        actual = Hex.axialToWorld(new Vector2(1, 1));
        Assert.assertEquals(new Vector3(2.598076f, 0, 1.5f), actual);
        actual = Hex.axialToWorld(new Vector2(-1, 2));
        Assert.assertEquals(new Vector3(0, 0, 3.0f), actual);
        actual = Hex.axialToWorld(new Vector2(10, -32));
        Assert.assertEquals(new Vector3(-10.392304f, 0, -48.0f), actual);
        actual = Hex.axialToWorld(new Vector2(-10, -32));
        Assert.assertEquals(new Vector3(-45.03332f, 0, -48.0f), actual);
    }

    @Ignore
    @Test
    public void testWorldToAxial() {
        Vector2 actual = Hex.worldToAxial(new Vector3(0, 0, 0));
        Assert.assertEquals(Vector2.Zero, actual);
        actual = Hex.worldToAxial(new Vector3(2.598076f, 0, 1.5f));
        Assert.assertEquals(new Vector2(0.99999999f, 0.99999999f), actual);        
        actual = Hex.worldToAxial(new Vector3(0, 0, 3.0f));
        Assert.assertEquals(new Vector2(-1, 2), actual);
        actual = Hex.worldToAxial(new Vector3(-10.392304f, 0, -48.0f));
        Assert.assertEquals(new Vector2(10, -32), actual);
        actual = Hex.worldToAxial(new Vector3(-45.03332f, 0, -48.0f));
        Assert.assertEquals(new Vector2(-10, -32), actual);
    }    
}