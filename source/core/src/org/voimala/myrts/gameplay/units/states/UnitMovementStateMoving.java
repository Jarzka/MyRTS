package org.voimala.myrts.gameplay.units.states;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.Unit;

public class UnitMovementStateMoving extends UnitMovementState {
    public UnitMovementStateMoving(Unit ownerUnit) {
        super(ownerUnit);
    }

    @Override
    public void update() {
        Vector2 nextPoint = ownerUnit.getPathPoints().get(0);

        if (hasReachedPoint(nextPoint)) {
            ownerUnit.changeMovementState(new UnitMovementStateStopped(ownerUnit));
        } else {
            moveTowardsPoint(nextPoint);
        }
    }

    private boolean hasReachedPoint(Vector2 nextPoint) {
        return ownerUnit.getX() == nextPoint.x
                && ownerUnit.getY() == nextPoint.y;
    }

    private void moveTowardsPoint(Vector2 nextPoint) {
        // TODO
    }
}
