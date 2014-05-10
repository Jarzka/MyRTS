package org.voimala.myrts.gameplay.units.states.tests;

import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.myrts.gameplay.units.states.UnitMovementStateMoving;

import static org.junit.Assert.*;

public class UnitMovementStateMovingTest {

    @org.junit.Test
    public void testUnitMoves() {
        Unit unit = new Unit();

        float angleOld = unit.getAngle();
        float xOld = unit.getX();
        float yOld = unit.getY();

        UnitMovementStateMoving state = new UnitMovementStateMoving(unit);
        unit.changeMovementState(state);
        state.update();

        // Unit has moved
        assertTrue(unit.getAngle() != angleOld
                        || unit.getY() != xOld
                        || unit.getY() != yOld
        );
    }
}