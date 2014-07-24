package org.voimala.myrts.screens.gameplay.units.turrets;

import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

import java.util.ArrayList;

public class TurretStateIdle extends AbstractTurretState {

    private long timeSpentSinceLastAttemptToTryToFindNewTargetMs = 0;
    private final long findNewTargetIdleMs = 500;

    public TurretStateIdle(AbstractTurret owner) {
        super(owner);
    }

    @Override
    public void updateState(final float deltaTime) {
        timeSpentSinceLastAttemptToTryToFindNewTargetMs += deltaTime * 1000;
        rotateTowardsOwnerUnit();
        findNewClosestTarget();
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

    /** Does nothing if less than 0.5 seconds have passed since last call */
    private void findNewClosestTarget() {
        // This is somewhat time consuming method so do not run it on every world update.
        if (timeSpentSinceLastAttemptToTryToFindNewTargetMs >= findNewTargetIdleMs) {
            timeSpentSinceLastAttemptToTryToFindNewTargetMs = 0;

            // Target is null if nothing is found.
            ownerTurret.setTarget(findClosestEnemyInRange());

            changeStateIfTargetFound();
        }
    }

    private void changeStateIfTargetFound() {
        if (ownerTurret.hasTarget()) {
            ownerTurret.setState(new TurretStateHasTarget(ownerTurret));
        }
    }

    protected AbstractUnit findClosestEnemyInRange() {
        // TODO Very time consuming method
        // Find all units in range
        ArrayList<AbstractUnit> targetsInRange = new ArrayList<AbstractUnit>();
        for (AbstractUnit unit : ownerTurret.getWorldController().getUnitContainerAllUnits().getUnits()) {
            if (unit.getTeam() == ownerTurret.getOwnerUnit().getTeam()) {
                continue;
            }

            if (MathHelper.getDistanceBetweenPoints(ownerTurret.getPosition().x,
                    ownerTurret.getPosition().y,
                    unit.getX(),
                    unit.getY()) <= ownerTurret.getRange()) {
                targetsInRange.add(unit);
            }
        }

        // Find the closest one

        AbstractUnit currentClosestTarget = null;
        if (!targetsInRange.isEmpty()) {
            currentClosestTarget = targetsInRange.get(0);
        }

        for (AbstractUnit unit : targetsInRange) {
            if (MathHelper.getDistanceBetweenPoints(
                    ownerTurret.getPosition().x,
                    ownerTurret.getPosition().y,
                    unit.getX(),
                    unit.getY()) <
                    MathHelper.getDistanceBetweenPoints(
                            ownerTurret.getPosition().x,
                            ownerTurret.getPosition().y,
                            currentClosestTarget.getX(),
                            currentClosestTarget.getY())) {
                currentClosestTarget = unit;
            }
        }

        return currentClosestTarget;
    }

}
