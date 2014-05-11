package org.voimala.utility;

import org.junit.Assert;
import org.junit.Test;

public class MathHelperTest {

    @Test
    public void testGetFasterTurningDirection1() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, 10, 10)),
                1);
    }

    @Test
    public void testGetFasterTurningDirection2() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI * 2,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, 10, 10)),
                2);
    }

    @Test
    public void testGetFasterTurningDirection3() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI * 2,
                        MathHelper.getAngleBetweenPointsInRadians(10, 10, -235, -151)),
                1);
    }

    @Test
    public void testGetFasterTurningDirection4() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(0,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, -10, 10)),
                2);
    }

    @Test
    public void testGetFasterTurningDirection5() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI / 10,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, 10, 10)),
                2);
    }

    @Test
    public void testGetFasterTurningDirection6() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI * 1.5,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, 10, 100)),
                2);
    }

    @Test
    public void testGetFasterTurningDirection7() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(0,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, -10, -10)),
                1);
    }

    @Test
    public void testGetFasterTurningDirection8() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI / 2,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, -10, -10)),
                2);
    }

    @Test
    public void testGetFasterTurningDirection9() {
        Assert.assertEquals(MathHelper.getFasterTurningDirection(Math.PI,
                        MathHelper.getAngleBetweenPointsInRadians(0, 0, -10, 10)),
                1);
    }

    @Test
    public void testRound1() {
        Assert.assertTrue(MathHelper.round(1.127, 2) == 1.13);
    }

    @Test
    public void testRound2() {
        Assert.assertTrue(MathHelper.round(1.1, 2) == 1.1);
    }

    @Test
    public void testRound3() {
        Assert.assertTrue(MathHelper.round(11.123456, 5) == 11.12346);
    }

    @Test
    public void testRound4() {
        Assert.assertTrue(MathHelper.round(12.17, 1) == 12.2);
    }

    @Test
    public void testRound5() {
        Assert.assertTrue(MathHelper.round(144.111, 2) == 144.11);
    }

    @Test
    public void testRound6() {
        Assert.assertTrue(MathHelper.round(144.163436, 1) == 144.2);
    }

    @Test
    public void testRound7() {
        Assert.assertTrue(MathHelper.round(144.163436, 2) == 144.16);
    }

}