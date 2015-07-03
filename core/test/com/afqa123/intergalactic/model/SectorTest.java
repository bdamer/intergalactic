package com.afqa123.intergalactic.model;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.utils.Json;
import org.junit.Assert;
import org.junit.Test;

public class SectorTest {
 
    @Test
    public void testSerialization() {
        Sector expected = new Sector("Beeblebrox", new HexCoordinate(10, 10), StarType.BLUE);
        Json json = new Json();
        String raw = json.toJson(expected);
        Sector actual = json.fromJson(Sector.class, raw);
        Assert.assertEquals(expected.getCoordinates(), actual.getCoordinates());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getType(), actual.getType());
    }    
}