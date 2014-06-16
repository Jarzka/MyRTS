package org.voimala.myrts.screens.gameplay.units.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

public class CarMovement extends AbstractMovement {

    private double acceleratorPedal = 0; /// 0 = no acceleration, 1 = full acceleration.
    private double steeringWheel = 0; // 1 = full clockwise, -1 = full counter-clockwise
    private int currentRotationDirection = 0; // 1 = clockwise, -1 = counter-clockwise
    private boolean stopAtFinalPoint = true;

    public CarMovement(AbstractUnit ownerUnit) {
        super(ownerUnit);
    }

    public void update(final float deltaTime) {
        handleMotion(deltaTime);
        handleAI(deltaTime);
    }

    private void handleMotion(float deltaTime) {
        handleAcceleration(deltaTime);
        handleDeceleration(deltaTime);
        handleVelocity(deltaTime);

        handleRotationAcceleration(deltaTime);
        handleRotationDeceleration(deltaTime);
        handleRotationVelocity(deltaTime);
    }

    private void handleVelocity(float deltaTime) {
        ownerUnit.moveX(Math.cos(ownerUnit.getAngleInRadians()) * currentVelocity * deltaTime);
        ownerUnit.moveY(Math.sin(ownerUnit.getAngleInRadians()) * currentVelocity * deltaTime);
    }

    private void handleAcceleration(final float deltaTime) {
        if (acceleratorPedal > 0) {
            currentVelocity += acceleration * deltaTime;

            if (currentVelocity > maxVelocity) {
                currentVelocity = maxVelocity;
            }
        }
    }

    private void handleDeceleration(final float deltaTime) {
        if (acceleratorPedal == 0) {
            currentVelocity -= deceleration * deltaTime;

            if (currentVelocity < 0) {
                currentVelocity = 0;
            }
        }
    }

    private void handleRotationVelocity(float deltaTime) {
        // Change current rotation direction if rotation is stopped and steering wheel is turned.
        if (currentRotationVelocity == 0) {
            if (steeringWheel > 0) {
                currentRotationDirection = 1;
            } else if (steeringWheel < 0) {
                currentRotationDirection = -1;
            }
        }

        // Handle current rotation direction
        if (currentRotationDirection == 1) {
            ownerUnit.rotate(-(float) (deltaTime * currentRotationVelocity));
        } else if (currentRotationDirection == -1) {
            ownerUnit.rotate((float) (deltaTime * currentRotationVelocity));
        }
    }

    private void handleRotationAcceleration(final float deltaTime) {
        if ((steeringWheel > 0 && currentRotationDirection == 1)
                || (steeringWheel < 0 && currentRotationDirection == -1)) {
            currentRotationVelocity += rotationAcceleration * deltaTime;

            if (currentRotationVelocity > maxRotationVelocity) {
                currentRotationVelocity = maxRotationVelocity;
            }
        }
    }

    private void handleRotationDeceleration(final float deltaTime) {
        if (steeringWheel == 0
                || (steeringWheel > 0 && currentRotationDirection != 1)
                || (steeringWheel < 0 && currentRotationDirection != -1)) {
            currentRotationVelocity -= rotationDeceleration * deltaTime;

            if (currentRotationVelocity < 0) {
                currentRotationVelocity = 0;
            }
        }
    }

    private void handleAI(float deltaTime) {
        if(!pathPoints.isEmpty()) {
            drive(deltaTime);
        } else {
            if (currentVelocity > 0) {
                stop(deltaTime);
            }
        }
    }

    private void drive(final float deltaTime) {
        acceleratorPedal = 1;

        Vector2 nextPoint = pathPoints.get(0);
        driveTowardsPoint(deltaTime, nextPoint);
        predictMovement(nextPoint);

        if (hasReachedPoint(nextPoint)) {
            pathPoints.remove(nextPoint);
        }
    }

    private void driveTowardsPoint(final float deltaTime, final Vector2 nextPoint) {
        rotateTowardsPoint(deltaTime, nextPoint);
    }

    private void predictMovement(final Vector2 nextPoint) {
        stopAtFinalPoint(nextPoint);
    }

    private void stopAtFinalPoint(final Vector2 nextPoint) {
        if (stopAtFinalPoint && pathPoints.size() == 1) {
            // Release accelerator at the right time so that the car stops at the final point

            // How much time does it take for the car to stop
            double timeToStopInSeconds = currentVelocity / deceleration;

            // Calculate distance between current point and the next (final) point
            double distanceBetweenCurrentPointAndEndPoint = MathHelper.getDistanceBetweenPoints(
                    ownerUnit.getX(), ownerUnit.getY(), nextPoint.x, nextPoint.y);

            if (distanceBetweenCurrentPointAndEndPoint <= currentVelocity * timeToStopInSeconds) {
                acceleratorPedal = 0;
            }
        }
    }

    private void rotateTowardsPoint(final float deltaTime, final Vector2 point) {
        double angleBetweenUnitAndPointInRadians = MathHelper.getAngleBetweenPointsInRadians(
                ownerUnit.getX(),
                ownerUnit.getY(),
                point.x,
                point.y);

        // If unit is not looking at the point, set the correct rotation direction
        if (MathHelper.round(ownerUnit.getAngle(), 1)
                != MathHelper.round(Math.toDegrees(angleBetweenUnitAndPointInRadians), 1)) {
            RotationDirection rotationDirection = MathHelper.getFasterTurningDirection(ownerUnit.getAngleInRadians(),
                    angleBetweenUnitAndPointInRadians);

            if (rotationDirection == RotationDirection.CLOCKWISE) {
                this.steeringWheel = 1;
            } else if (rotationDirection == RotationDirection.COUNTERCLOCKWISE) {
                this.steeringWheel = -1;
            }

            // Stop the rotation at the right time so that the rotation stops at the final angle

            // How much time does it take for the car to stop rotation
            double timeToStopRotationInSeconds = currentRotationVelocity / rotationDeceleration;

            // Calculate distance between current angle and the next (final) angle
            double distanceBetweenCurrentAngleAndEndAngle = MathHelper.getDistanceFromAngle1ToAngle2(
                    ownerUnit.getAngleInRadians(),
                    angleBetweenUnitAndPointInRadians,
                    rotationDirection);
            double distanceBetweenCurrentAngleAndEndAngleDegree =
                    Math.toDegrees(distanceBetweenCurrentAngleAndEndAngle);

            // TODO
            if (distanceBetweenCurrentAngleAndEndAngleDegree <= currentRotationVelocity * timeToStopRotationInSeconds) {
                this.steeringWheel = 0;
            }
        } else {
            this.steeringWheel = 0;
        }
    }

    private void stop(final float deltaTime) {
        acceleratorPedal = 0;
        steeringWheel = 0;
    }
}
