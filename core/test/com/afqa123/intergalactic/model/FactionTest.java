package com.afqa123.intergalactic.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import org.junit.Assert;
import org.junit.Test;

public class FactionTest {

    @Test
    public void testSerialization() {
        Galaxy g = new Galaxy(1);
        Faction expected = new Faction("name", Color.RED, true, g);
        
        Json json = new Json();
        String raw = json.toJson(expected);
        Faction actual = json.fromJson(Faction.class, raw);
        
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getColor(), actual.getColor());
        Assert.assertEquals(expected.isPlayer(), actual.isPlayer());

        // TODO: compare faction map
        
    }
    
}
