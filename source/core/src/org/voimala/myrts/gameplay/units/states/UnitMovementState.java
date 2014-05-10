package org.voimala.myrts.gameplay.units.states;

import org.voimala.myrts.gameplay.units.Unit;

public abstract class UnitMovementState {
    Unit ownerUnit = null;

    public UnitMovementState(Unit ownerUnit) {
        this.ownerUnit = ownerUnit;
    }

    public abstract void update();
}
