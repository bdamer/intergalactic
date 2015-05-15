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
        List<HexCoordinate> actual = c.getRing(1);
        Assert.assertEquals(new HexCoordinate(-1, 1), actual.get(0));
        Assert.assertEquals(new HexCoordinate(0, 1), actual.get(1));
        Assert.assertEquals(new HexCoordinate(1, 0), actual.get(2));
        Assert.assertEquals(new HexCoordinate(1, -1), actual.get(3));
        Assert.assertEquals(new HexCoordinate(0, -1), actual.get(4));
        Assert.assertEquals(new HexCoordinate(-1, 0), actual.get(5));
    }
    
    @Test
    public void testGetRing3() {
        HexCoordinate c = new HexCoordinate(0, 0);
        List<HexCoordinate> actual = c.getRing(3);
        Assert.assertEquals(new HexCoordinate(-3, 3), actual.get(0));
        Assert.assertEquals(new HexCoordinate(0, 3), actual.get(3));
        Assert.assertEquals(new HexCoordinate(3, 0), actual.get(6));
        Assert.assertEquals(new HexCoordinate(3, -3), actual.get(9));
        Assert.assertEquals(new HexCoordinate(0, -3), actual.get(12));
        Assert.assertEquals(new HexCoordinate(-3, 0), actual.get(15));
    }    
}