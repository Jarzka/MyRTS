package org.voimala.myrts.gameplay.units;

import org.junit.Test;
import org.voimala.utility.MathHelper;

import static org.junit.Assert.*;

public class UnitTest {

    @Test
    public void testGetAngleInRadians1() {
        Unit unit = new Unit();
        unit.setAngle(0);

        assertTrue(MathHelper.round(unit.getAngleInRadians(), 1) == 0);
    }

    @Test
    public void testGetAngleInRadians2() {
        Unit unit = new Unit();
        unit.setAngle(90);

        assertTrue(MathHelper.round(unit.getAngleInRadians(), 2) == MathHelper.round(Math.PI / 2, 2));
    }

    @Test
    public void testGetAngleInRadians3() {
        Unit unit = new Unit();
        unit.setAngle(180);

        assertTrue(MathHelper.round(unit.getAngleInRadians(), 2) == MathHelper.round(Math.PI, 2));
    }
}