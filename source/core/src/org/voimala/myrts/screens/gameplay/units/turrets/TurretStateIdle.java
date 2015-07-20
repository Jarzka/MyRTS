package org.voimala.myrts.screens.gameplay.units.turrets;

import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

public class TurretStateIdle extends AbstractTurretState {

    private long timeSpentSinceLastAttemptToTryToFindNewTargetMs = 0;
    private final long findNewTargetIdleMs = 50; // Saves time but causes turrets to lag.
    private long timeSpentWithoutHavingTargetMs = 0;

    public TurretStateIdle(AbstractTurret owner) {
        super(owner);
    }

    @Override
    public void updateState(final float deltaTime) {
        timeSpentWithoutHavingTargetMs += deltaTime * 1000;
        if (timeSpentWithoutHavingTargetMs > 1000) {
            handleLogicalRotation();
        } else {
            ownerTurret.setSteeringWheel(0);
        }

        timeSpentSinceLastAttemptToTryToFindNewTargetMs += deltaTime * 1000;
        if (timeSpentSinceLastAttemptToTryToFindNewTargetMs >= findNewTargetIdleMs) {
            timeSpentSinceLastAttemptToTryToFindNewTargetMs = 0;
            ownerTurret.findNewClosestTarget();
            changeStateIfTargetFound(); // This is time consuming method so do not run it on every world update.
        }
    }

    private void handleLogicalRotation() {
        rotateTowardsOwnerUnit();
    }

    private void rotateTowardsOwnerUnit() {
        // If the turret is not looking at the same direction as the owner, set the correct rotation direction
        if (MathHelper.round(ownerTurret.getAngle(), 0)
                != MathHelper.round(ownerTurret.getOwnerUnit().getAngle(), 0)) {
            RotationDirection targetRotationDirection = MathHelper.getFasterTurningDirection(ownerTurret.getAngleInRadians(),
                    ownerTurret.getOwnerUnit().getAngleInRadians());

            if (targetRotationDirection == RotationDirection.CLOCKWISE) {
                ownerTurret.setSteeringWheel(1);
            } else if (targetRotationDirection == RotationDirection.COUNTERCLOCKWISE) {
                ownerTurret.setSteeringWheel(-1);
            }

            // Stop the rotation at the right time so that the rotation stops at the final angle

            // How much time does it take to stop rotation
            double timeToStopRotationInSeconds = ownerTurret.getCurrentRotationVelocity() / ownerTurret.getRotationDeceleration();

            // Calculate distance between current angle and the next (final) angle
            double distanceBetweenCurrentAngleAndTargetAngle = MathHelper.getDistanceFromAngle1ToAngle2(
                    ownerTurret.getAngleInRadians(),
                    ownerTurret.getOwnerUnit().getAngleInRadians(),
                    targetRotationDirection);
            double distanceBetweenCurrentAngleAndTargetAngleDegree =
                    Math.toDegrees(distanceBetweenCurrentAngleAndTargetAngle);

            if (distanceBetweenCurrentAngleAndTargetAngleDegree <= ownerTurret.getCurrentRotationVelocity() * timeToStopRotationInSeconds) {
                ownerTurret.setSteeringWheel(0);
            }
        } else {
            ownerTurret.setSteeringWheel(0);
        }
    }



    private void changeStateIfTargetFound() {
        if (ownerTurret.hasTarget()) {
            ownerTurret.setState(new TurretStateHasTarget(ownerTurret));
        }
    }

}
