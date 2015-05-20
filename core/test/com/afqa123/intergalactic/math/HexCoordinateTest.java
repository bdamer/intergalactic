package com.afqa123.intergalactic.math;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class HexCoordinateTest {

    @Test
    public void testGetNeighbor() {
        HexCoordinate c = new HexCoordinate(0, 0);
        Assert.assertEquals(new HexCoordinate(1, 0), c.getNeighbor(HexCoordinate.Direction.EAST));
        Assert.assertEquals(new HexCoordinate(1, -1), c.getNeighbor(HexCoordinate.Direction.NORTH_EAST));
        Assert.assertEquals(new HexCoordinate(0, -1), c.getNeighbor(HexCoordinate.Direction.NORTH_WEST));
        Assert.assertEquals(new HexCoordinate(-1, 0), c.getNeighbor(HexCoordinate.Direction.WEST));
        Assert.assertEquals(new HexCoordinate(-1, 1), c.getNeighbor(HexCoordinate.Direction.SOUTH_WEST));
        Assert.assertEquals(new HexCoordinate(0, 1), c.getNeighbor(HexCoordinate.Direction.SOUTH_EAST));
    }

    @Test
    public void testGetRing1() {
        HexCoordinate c = new HexCoordinate(0, 0);
        HexCoordinate[] actual = c.getRing(1);
        Assert.assertEquals(new HexCoordinate(-1, 1), actual[0]);
        Assert.assertEquals(new HexCoordinate(0, 1), actual[1]);
        Assert.assertEquals(new HexCoordinate(1, 0), actual[2]);
        Assert.assertEquals(new HexCoordinate(1, -1), actual[3]);
        Assert.assertEquals(new HexCoordinate(0, -1), actual[4]);
        Assert.assertEquals(new HexCoordinate(-1, 0), actual[5]);
    }
    
    @Test
    public void testGetRing3() {
        HexCoordinate c = new HexCoordinate(0, 0);
        HexCoordinate[] actual = c.getRing(3);
        Assert.assertEquals(new HexCoordinate(-3, 3), actual[0]);
        Assert.assertEquals(new HexCoordinate(0, 3), actual[3]);
        Assert.assertEquals(new HexCoordinate(3, 0), actual[6]);
        Assert.assertEquals(new HexCoordinate(3, -3), actual[9]);
        Assert.assertEquals(new HexCoordinate(0, -3), actual[12]);
        Assert.assertEquals(new HexCoordinate(-3, 0), actual[15]);
    }    
}