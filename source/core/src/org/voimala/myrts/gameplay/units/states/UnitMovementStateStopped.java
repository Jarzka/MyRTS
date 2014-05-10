package org.voimala.myrts.gameplay.units.states;

import org.voimala.myrts.gameplay.units.Unit;

public class UnitMovementStateStopped extends UnitMovementState {
    public UnitMovementStateStopped(Unit ownerUnit) {
        super(ownerUnit);
    }

    @Override
    public void update() {
        if(!ownerUnit.getPathPoints().isEmpty()) {
            ownerUnit.changeMovementState(new UnitMovementStateMoving(ownerUnit));
        }
    }
}
