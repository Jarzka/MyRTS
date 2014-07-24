package org.voimala.myrts.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

public class CarMovement extends AbstractMovement {

    private double acceleratorPedal = 0; /// 0 = no acceleration, 1 = full acceleration.
    private double steeringWheel = 0; // 1 = full clockwise, -1 = full counter-clockwise

    public CarMovement(AbstractUnit owner) {
        super(owner);
    }

    public void update(final float deltaTime) {
        handlePhysicalMotion(deltaTime);
        handleLogicalMotion(deltaTime);
    }

    private void handlePhysicalMotion(float deltaTime) {
        handlePhysicalAcceleration(deltaTime);
        handlePhysicalDeceleration(deltaTime);
        handlePhysicalVelocity(deltaTime);

        handlePhysicalRotationAcceleration(deltaTime);
        handlePhysicalRotationDeceleration(deltaTime);
        handlePhysicalRotationVelocity(deltaTime);
    }

    private void handlePhysicalVelocity(float deltaTime) {
        Vector2 nextPosition = new Vector2(
                (float) (owner.getX() + Math.cos(owner.getAngleInRadians()) * currentVelocity * deltaTime),
                (float) (owner.getY() + Math.sin(owner.getAngleInRadians()) * currentVelocity * deltaTime));

        // Make sure that the next position is not inside obstacle

        boolean isCollisionDetected = false;
        /* TODO Currently buggy
        for (AbstractUnit unit : owner.getWorldController().getUnitContainer().getAllUnits()) {
            if (unit == owner) {
                continue;
            }

            if (unit.onCollision(nextPosition)) {
                isCollisionDetected = true;
                break;
            }
        }
        */

        // TODO Count buildings, trees, rocks etc. too

        if (!isCollisionDetected) {
            owner.setPosition(nextPosition);
        }
    }

    private void handlePhysicalAcceleration(final float deltaTime) {
        if (acceleratorPedal > 0) {
            currentVelocity += acceleration * deltaTime;

            if (currentVelocity > maxVelocity) {
                currentVelocity = maxVelocity;
            }
        }
    }

    private void handlePhysicalDeceleration(final float deltaTime) {
        if (acceleratorPedal == 0) {
            currentVelocity -= deceleration * deltaTime;

            if (currentVelocity < 0) {
                currentVelocity = 0;
            }
        }
    }

    private void handlePhysicalRotationVelocity(float deltaTime) {
        // Change current rotation direction if rotation is stopped and steering wheel is turned.
        if (currentRotationVelocity == 0) {
            if (steeringWheel > 0) {
                currentRotationDirection = RotationDirection.CLOCKWISE;
            } else if (steeringWheel < 0) {
                currentRotationDirection = RotationDirection.COUNTERCLOCKWISE;
            }
        }

        // Handle current rotation direction
        if (currentRotationDirection == RotationDirection.CLOCKWISE) {
            owner.rotate(-(float) (deltaTime * currentRotationVelocity));
        } else if (currentRotationDirection == RotationDirection.COUNTERCLOCKWISE) {
            owner.rotate((float) (deltaTime * currentRotationVelocity));
        }
    }

    private void handlePhysicalRotationAcceleration(final float deltaTime) {
        if ((steeringWheel > 0 && currentRotationDirection == RotationDirection.CLOCKWISE)
                || (steeringWheel < 0 &&currentRotationDirection == RotationDirection.COUNTERCLOCKWISE)) {
            currentRotationVelocity += rotationAcceleration * deltaTime;

            if (currentRotationVelocity > maxRotationVelocity) {
                currentRotationVelocity = maxRotationVelocity;
            }
        }
    }

    private void handlePhysicalRotationDeceleration(final float deltaTime) {
        if (steeringWheel == 0
                || (steeringWheel > 0 && currentRotationDirection != RotationDirection.CLOCKWISE)
                || (steeringWheel < 0 && currentRotationDirection != RotationDirection.COUNTERCLOCKWISE)) {
            currentRotationVelocity -= rotationDeceleration * deltaTime;

            if (currentRotationVelocity < 0) {
                currentRotationVelocity = 0;
            }
        }
    }

    private void handleLogicalMotion(float deltaTime) {
        if(!pathPoints.isEmpty()) {
            drive(deltaTime);
        } else {
            if (currentVelocity > 0) {
                stop(deltaTime);
            }
        }
    }

    private void drive(final float deltaTime) {
        Vector2 nextPoint = pathPoints.get(0);
        driveTowardsPoint(deltaTime, nextPoint);
        stopAtFinalPoint(nextPoint);

        if (hasReachedPoint(nextPoint)) {
            pathPoints.remove(nextPoint);
        }
    }

    private void driveTowardsPoint(final float deltaTime, final Vector2 nextPoint) {
        acceleratorPedal = 1;
        rotateTowardsPoint(deltaTime, nextPoint);
    }

    private void rotateTowardsPoint(final float deltaTime, final Vector2 point) {
        double angleBetweenUnitAndPointInRadians = MathHelper.getAngleBetweenPointsInRadians(
                owner.getX(),
                owner.getY(),
                point.x,
                point.y);

        // If unit is not looking at the point, set the correct rotation direction
        if (MathHelper.round(owner.getAngle(), 0)
                != MathHelper.round(Math.toDegrees(angleBetweenUnitAndPointInRadians), 0)) {
            RotationDirection targetRotationDirection = MathHelper.getFasterTurningDirection(owner.getAngleInRadians(),
                    angleBetweenUnitAndPointInRadians);

            if (targetRotationDirection == RotationDirection.CLOCKWISE) {
                this.steeringWheel = 1;
            } else if (targetRotationDirection == RotationDirection.COUNTERCLOCKWISE) {
                this.steeringWheel = -1;
            }

            // Stop the rotation at the right time so that the rotation stops at the final angle

            // How much time does it take to stop rotation
            double timeToStopRotationInSeconds = currentRotationVelocity / rotationDeceleration;

            // Calculate distance between current angle and the next (final) angle
            double distanceBetweenCurrentAngleAndTargetAngle = MathHelper.getDistanceFromAngle1ToAngle2(
                    owner.getAngleInRadians(),
                    angleBetweenUnitAndPointInRadians,
                    targetRotationDirection);
            double distanceBetweenCurrentAngleAndTargetAngleDegree =
                    Math.toDegrees(distanceBetweenCurrentAngleAndTargetAngle);

            if (distanceBetweenCurrentAngleAndTargetAngleDegree <= currentRotationVelocity * timeToStopRotationInSeconds) {
                this.steeringWheel = 0;
            }
        } else {
            this.steeringWheel = 0;
        }
    }


    private void stopAtFinalPoint(final Vector2 nextPoint) {
        if (pathPoints.size() == 1) {
            // Release accelerator at the right time so that the car stops at the final point

            // How much time does it take for the car to stop
            double timeToStopInSeconds = currentVelocity / deceleration;

            // Calculate distance between current point and the next (final) point
            double distanceBetweenCurrentPointAndTargetPoint = MathHelper.getDistanceBetweenPoints(
                    owner.getX(), owner.getY(), nextPoint.x, nextPoint.y);

            if (distanceBetweenCurrentPointAndTargetPoint <= currentVelocity * timeToStopInSeconds) {
                acceleratorPedal = 0;
            }
        }
    }

    private void stop(final float deltaTime) {
        acceleratorPedal = 0;
        steeringWheel = 0;
    }

}
