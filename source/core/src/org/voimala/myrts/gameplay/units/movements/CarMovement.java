package org.voimala.myrts.gameplay.units.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.utility.MathHelper;

import java.util.ArrayList;

public class CarMovement extends Movement {
    private ArrayList<Vector2> pathPoints = new ArrayList<>();

    public CarMovement(final Unit ownerUnit,
                       final double acceleration,
                       final double deceleration,
                       final double rotationVelocity) {
        super(ownerUnit, acceleration, deceleration, rotationVelocity);
    }

    public CarMovement(final Unit ownerUnit) {
        super(ownerUnit);
    }

    public void update(final float deltaTime) {
        if(!pathPoints.isEmpty()) {
            move(deltaTime);
        }
    }

    private void move(final float deltaTime) {
        Vector2 nextPoint = pathPoints.get(0);
        moveTowardsPoint(deltaTime, nextPoint);

        if (hasReachedPoint(nextPoint)) {
            pathPoints.clear();
        }
    }

    private void moveTowardsPoint(final float deltaTime, final Vector2 nextPoint) {
        rotateTowardsPoint(deltaTime, nextPoint);

        // TODO
        //double angleFromUnitToTarget = getAngleFromUnitToTarget(nextPoint);
        //ownerUnit.setX();
        //ownerUnit.setY();
    }

    private void rotateTowardsPoint(final float deltaTime,  final Vector2 point) {
        int rotationDirection = 1; // TODO

        if (rotationDirection == 2) {
            ownerUnit.rotate((float) (deltaTime * rotationVelocity));
        } else if (rotationDirection == 1) {
            ownerUnit.rotate(-(float) (deltaTime * rotationVelocity));
        }
    }

    public void addPathPoint(final Vector2 point) {
        pathPoints.add(point);
    }

    public ArrayList<Vector2> getPathPoints() {
        return pathPoints;
    }
}
