package org.voimala.myrts.gameplay.units;

import org.voimala.myrts.gameplay.units.movements.CarMovement;

import static org.junit.Assert.*;

public class MovementTest {

    @org.junit.Test
    public void testUnitMoves() {
        Unit unit = new Unit();
        unit.setMovement(new CarMovement(unit, 10, 10, 10));

        float angleOld = unit.getAngle();
        float xOld = unit.getX();
        float yOld = unit.getY();

        unit.getMovement().update(10);

        // Unit has moved
        assertTrue(unit.getAngle() != angleOld
                        || unit.getY() != xOld
                        || unit.getY() != yOld
        );
    }
}