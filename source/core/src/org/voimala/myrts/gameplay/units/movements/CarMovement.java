package org.voimala.myrts.gameplay.units.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.utility.MathHelper;

import java.util.ArrayList;

public class CarMovement extends Movement {
    private ArrayList<Vector2> pathPoints = new ArrayList<>();

    public CarMovement(Unit ownerUnit) {
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
            pathPoints.remove(nextPoint);
        }
    }

    private void moveTowardsPoint(final float deltaTime, final Vector2 nextPoint) {
        rotateTowardsPoint(deltaTime, nextPoint);
        ownerUnit.moveX(Math.cos(ownerUnit.getAngleInRadians()) * maxVelocity * deltaTime);
        ownerUnit.moveY(Math.sin(ownerUnit.getAngleInRadians()) * maxVelocity * deltaTime);
    }

    private void rotateTowardsPoint(final float deltaTime, final Vector2 point) {
        double angleBetweenUnitAndPointInRadians = MathHelper.getAngleBetweenPointsInRadians(
                ownerUnit.getX(),
                ownerUnit.getY(),
                point.x,
                point.y);

        // If unit is not looking at the point, rotate it
        if (MathHelper.round(ownerUnit.getAngleInRadians(), 4)
                != MathHelper.round(angleBetweenUnitAndPointInRadians, 4)) {
            int rotationDirection = MathHelper.getFasterTurningDirection(ownerUnit.getAngleInRadians(),
                    angleBetweenUnitAndPointInRadians);

            if (rotationDirection == 1) {
                ownerUnit.rotate(-(float) (deltaTime * maxRotationVelocity));
            } else if (rotationDirection == 2) {
                ownerUnit.rotate((float) (deltaTime * maxRotationVelocity));
            }
        }
    }

    public void addPathPoint(final Vector2 point) {
        pathPoints.add(point);
    }

    public ArrayList<Vector2> getPathPoints() {
        return pathPoints;
    }
}
