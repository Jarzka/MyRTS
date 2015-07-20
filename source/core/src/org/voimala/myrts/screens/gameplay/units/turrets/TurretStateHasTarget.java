package org.voimala.myrts.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.audio.AudioEffect;
import org.voimala.myrts.audio.SoundContainer;
import org.voimala.myrts.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrts.screens.gameplay.effects.GeneralMuzzleFire;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

public class TurretStateHasTarget extends AbstractTurretState {

    private final long checkIfTargetIsInSightLagMs = 200;
    private long timeSpentSinceLastCheckIfTargetIsInSight = 0;
    private boolean isTargetInSight = false;

    public TurretStateHasTarget(final AbstractTurret owner) {
        super(owner);
    }

    @Override
    public void updateState(final float deltaTime) {
        checkTarget(deltaTime);
        handleLogicalRotation();
    }

    private void checkTarget(final float deltaTime) {
        checkIfTargetIsDead();
        checkIfTargetIsInRange();

        timeSpentSinceLastCheckIfTargetIsInSight += deltaTime * 1000;
        if (timeSpentSinceLastCheckIfTargetIsInSight >= checkIfTargetIsInSightLagMs) {
            timeSpentSinceLastCheckIfTargetIsInSight = 0;
            checkIfTargetIsInSight(25); // This is time consuming method so do not run it on every world update.
        }

        checkIfTargetCanBeShot();

        if (!ownerTurret.hasTarget()) {
            ownerTurret.findClosestEnemyInRange(); // Check if there are more enemy units in range
            if (!ownerTurret.hasTarget()) {
                ownerTurret.setState(new TurretStateIdle(ownerTurret));
            }
        }
    }

    private boolean checkIfTargetIsDead() {
        if (ownerTurret.getTarget() == null) {

            return true;
        }

        if (ownerTurret.getTarget().isDead()) {
            ownerTurret.setTarget(null);
            return true;
        }

        return false;
    }

    private void checkIfTargetCanBeShot() {
        if (ownerTurret.hasTarget()) {
            if (isTurretRotatedTowardsTarget() && isTargetInSight) {
                tryToShoot();
            }
        }
    }

    private void tryToShoot() {
        float distanceBetweenOriginAndShootPosition = (float) MathHelper.getDistanceBetweenPoints(
                ownerTurret.getPosition().x,
                ownerTurret.getPosition().y,
                ownerTurret.getPosition().x + ownerTurret.getRelativeShootPosition().x,
                ownerTurret.getPosition().y + ownerTurret.getRelativeShootPosition().y);
        float angleBetweenOriginAndShootPosition = (float) MathHelper.getAngleBetweenPointsInRadians(
                ownerTurret.getPosition().x,
                ownerTurret.getPosition().y,
                ownerTurret.getPosition().x + ownerTurret.getRelativeShootPosition().x,
                ownerTurret.getPosition().y + ownerTurret.getRelativeShootPosition().y);

        Vector2 spawnPoint = new Vector2( // TODO Close, but not right
                (float) (ownerTurret.getPosition().x + Math.cos(ownerTurret.getAngleInRadians()
                        + angleBetweenOriginAndShootPosition) * distanceBetweenOriginAndShootPosition),
                (float) (ownerTurret.getPosition().y + Math.sin(ownerTurret.getAngleInRadians()
                        + angleBetweenOriginAndShootPosition) * distanceBetweenOriginAndShootPosition));
        AbstractAmmunition ammunition = ownerTurret.getWeapon().tryToShoot(
                ownerTurret.getWorldController(),
                spawnPoint,
                ownerTurret.getAngle(),
                ownerTurret.getWeapon().getWeaponOptions());

        // TODO Decrease accuracy? Use SimTick as hash?

        if (ammunition != null) { // Weapon was fired
            ownerTurret.getWorldController().getAmmunitionContainer().add(ammunition);

            GeneralMuzzleFire muzzleFire = new GeneralMuzzleFire(ownerTurret.getWorldController(), spawnPoint, ownerTurret.getAngle());
            ownerTurret.getWorldController().getEffectsContainer().add(muzzleFire);

            ownerTurret.getWorldController().getAudioEffectContainer().add(
                    new AudioEffect(
                            ownerTurret.getWorldController(),
                            SoundContainer.getInstance().getSound("m4"),
                            0.08f,
                            new Vector2(ownerTurret.getPosition().x, ownerTurret.getPosition().y)));
        }
    }

    public boolean checkIfTargetIsInRange() {

        if (ownerTurret.hasTarget()) {
            if (MathHelper.getDistanceBetweenPoints(
                    ownerTurret.getPosition().x,
                    ownerTurret.getPosition().y,
                    ownerTurret.getTarget().getX(),
                    ownerTurret.getTarget().getY()) <= ownerTurret.getRange()) {
                return true;
            }

            ownerTurret.setTarget(null);
        }

        return false;
    }

    public boolean isTurretRotatedTowardsTarget() {
        double angleBetweenTurretAndTargetInRadians = MathHelper.getAngleBetweenPointsInRadians(
                ownerTurret.getPosition().x,
                ownerTurret.getPosition().y,
                ownerTurret.getTarget().getPosition().x,
                ownerTurret.getTarget().getPosition().y);

        return MathHelper.round(ownerTurret.getAngle(), 0) == MathHelper.round(Math.toDegrees(angleBetweenTurretAndTargetInRadians), 0);
    }

    /**
     * Return true if there are now obstacles between the turret and the target.
     *
     * @param accuracy How many pixels the dot will be moved per loop.
     *                 Zero means very accurate and consumes lots of time. Max value is 200.
     */
    // TODO There is a faster way to implement this:
    // http://code.tutsplus.com/tutorials/quick-tip-collision-detection-between-a-circle-and-a-line-segment--active-10632
    // https://www.google.fi/?gws_rd=ssl#q=%22line+rectangle+collision+detection
    public void checkIfTargetIsInSight(int accuracy) {
        if (ownerTurret.hasTarget()) {
            if (accuracy > 200) {
                accuracy = 200;
            }

            /* TODO This is used mainly for infantry. Tanks' turret is higher than humans so humans are not
             * in the line of sight. This method needs to improved when tanks and other units are added in to the game. */

            // Create a dot
            Vector2 checkSight = new Vector2(ownerTurret.getPosition());
            double angleBetweenDotAndTargetRad = MathHelper.getAngleBetweenPointsInRadians(
                    checkSight.x,
                    checkSight.y,
                    ownerTurret.getTarget().getX(),
                    ownerTurret.getTarget().getY());

            boolean isCollisionDetected;
            checkSightLoop:
            while (true) {
                // Move the dot towards the target.
                checkSight.x = (float) (checkSight.x + Math.cos(angleBetweenDotAndTargetRad) * accuracy);
                checkSight.y = (float) (checkSight.y + Math.sin(angleBetweenDotAndTargetRad) * accuracy);

                // Check if there is a collision between the dot and some obstacle.
                for (AbstractUnit unit : ownerTurret.getWorldController().getUnitContainer().getAllUnits()) {
                    if (unit == ownerTurret.getOwnerUnit()
                            || unit == ownerTurret.getTarget()
                            || unit.getTeam() != ownerTurret.getOwnerUnit().getTeam()) {
                        continue;
                    }

                    if (unit.onCollision(checkSight)) {
                        isCollisionDetected = true;
                        break checkSightLoop;
                    }
                }

                // TODO Check also other obstacles like buildings, trees, rocks etc.

                // Target position reached
                if (MathHelper.getDistanceBetweenPoints(checkSight.x, checkSight.y, ownerTurret.getTarget().getX(), ownerTurret.getTarget().getY()) <= accuracy) {
                    isCollisionDetected = false;
                    break;
                }
            }

            isTargetInSight = !isCollisionDetected;
        } else {
            isTargetInSight = false;
        }
    }

    private void handleLogicalRotation() {
        if (ownerTurret.hasTarget()) {
            rotateTowardsTarget();
        }
    }

    private void rotateTowardsTarget() {
        double angleBetweenTurretAndTargetInRadians = MathHelper.getAngleBetweenPointsInRadians(
                ownerTurret.getPosition().x,
                ownerTurret.getPosition().y,
                ownerTurret.getTarget().getPosition().x,
                ownerTurret.getTarget().getPosition().y);

        // If the turret is not looking at the point, set the correct rotation direction
        if (MathHelper.round(ownerTurret.getAngle(), 0)
                != MathHelper.round(Math.toDegrees(angleBetweenTurretAndTargetInRadians), 0)) {
            RotationDirection targetRotationDirection = MathHelper.getFasterTurningDirection(
                    ownerTurret.getAngleInRadians(),
                    angleBetweenTurretAndTargetInRadians);

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
                    angleBetweenTurretAndTargetInRadians,
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

}
