package com.afqa123.intergalactic.model;

import com.badlogic.gdx.utils.Json;
import org.junit.Assert;
import org.junit.Test;

public class FactionMapTest {

    @Test
    public void testSerialization() {
        Galaxy galaxy = new Galaxy(10);
        FactionMap expected = new FactionMap(galaxy);
        Json json = new Json();
        String raw = json.toJson(expected);
        FactionMap actual = json.fromJson(FactionMap.class, raw);
        Assert.assertEquals(expected.getSectors().length, actual.getSectors().length);
    }
    
}
