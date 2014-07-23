package org.voimala.myrtsengine.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.audio.AudioEffect;
import org.voimala.myrtsengine.audio.SoundContainer;
import org.voimala.myrtsengine.screens.gameplay.effects.GeneralMuzzleFire;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

import java.util.ArrayList;

public abstract class AbstractTurret extends AbstractGameObject implements Cloneable {

    protected AbstractUnit owner;
    protected AbstractUnit target;
    protected AbstractWeapon weapon;

    protected double currentRotationVelocity = 0; /// deg/s
    protected double maxRotationVelocity = 0; /// deg/s
    protected double rotationAcceleration = 0; /// deg/s
    protected double rotationDeceleration = 0; /// deg/s
    protected RotationDirection currentRotationDirection;
    protected double steeringWheel = 0; // 1 = full clockwise, -1 = full counter-clockwise
    protected int accuracy = 0; // Accuracy in degrees (0 - 90);

    protected Vector2 relativePosition = new Vector2(0, 0); // Turrets position relative to the owner unit.
    protected Vector2 relativeShootPosition = new Vector2(0, 0); // Turrets shoot position relative to the owner unit.

    protected long range = 100;

    public AbstractTurret(AbstractUnit owner, AbstractWeapon weapon) {
        super(owner.getWorldController());
        this.owner = owner;
        this.position = new Vector2(owner.getX(), owner.getY());
        this.angleDeg = owner.getAngle();
        this.weapon = weapon;
    }

    /**
     * By default the clone will have the same owner as the original object. Target will be set to null.
     */
    public AbstractTurret clone() throws CloneNotSupportedException {
        AbstractTurret turretClone = (AbstractTurret) super.clone();

        AbstractWeapon weaponClone = weapon.clone();
        turretClone.setWeapon(weaponClone);

        turretClone.setRelativePosition(new Vector2(relativePosition.x, relativePosition.y));
        turretClone.setRelativeShootPosition(new Vector2(relativeShootPosition.x, relativeShootPosition.y));

        target = null;

        return turretClone;
    }

    public void updateState(final float deltaTime) {
        super.updateState(deltaTime);
        updateTurretState(deltaTime);
        updateWeaponState(deltaTime);
    }

    private void updateTurretState(final float deltaTime) {
        if (!hasTarget()) {
            findNewClosestTarget();
        }

        checkTarget();
        updatePosition();
        updateRotation(deltaTime);
    }

    protected void updatePosition() {
        position.x = owner.getX() + relativePosition.x;
        position.y = owner.getY() + relativePosition.y;
    }

    private void updateWeaponState(final float deltaTime) {
        if (weapon != null) {
            weapon.updateState(deltaTime);
        }
    }

    private void updateRotation(final float deltaTime) {
        handlePhysicalRotation(deltaTime);
        handleLogicalRotation();
    }

    private void handlePhysicalRotation(final float deltaTime) {
        handlePhysicalRotationAcceleration(deltaTime);
        handlePhysicalRotationDeceleration(deltaTime);
        handlePhysicalRotationVelocity(deltaTime);
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
            rotate(-(float) (deltaTime * currentRotationVelocity));
        } else if (currentRotationDirection == RotationDirection.COUNTERCLOCKWISE) {
            rotate((float) (deltaTime * currentRotationVelocity));
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

    private void handleLogicalRotation() {
        if (hasTarget()) {
            rotateTowardsTarget();
        } else {
            rotateTowardsOwnerUnit();
        }
    }

    private void rotateTowardsTarget() {
        double angleBetweenTurretAndTargetInRadians = MathHelper.getAngleBetweenPointsInRadians(
                position.x,
                position.y,
                target.getPosition().x,
                target.getPosition().y);

        // If the turret is not looking at the point, set the correct rotation direction
        if (MathHelper.round(getAngle(), 0)
                != MathHelper.round(Math.toDegrees(angleBetweenTurretAndTargetInRadians), 0)) {
            RotationDirection targetRotationDirection = MathHelper.getFasterTurningDirection(getAngleInRadians(),
                    angleBetweenTurretAndTargetInRadians);

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
                    getAngleInRadians(),
                    angleBetweenTurretAndTargetInRadians,
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

    private void rotateTowardsOwnerUnit() {
        // If the turret is not looking at the same direction as the owner, set the correct rotation direction
        if (MathHelper.round(angleDeg, 0)
                != MathHelper.round(owner.getAngle(), 0)) {
            RotationDirection targetRotationDirection = MathHelper.getFasterTurningDirection(getAngleInRadians(),
                    owner.getAngleInRadians());

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
                    getAngleInRadians(),
                    owner.getAngleInRadians(),
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

    private void checkTarget() {
        checkTargetIsStillInRange();

        if (hasTarget()) {
            if (turretIsRotatedTowardsTarget() && isTargetInSight(25)) {
                tryToShoot();
            } // If not, the logical rotation should rotate the turret towards the target
        }
    }

    private void checkTargetIsStillInRange() {
        if (hasTarget()) {
            if (!isTargetInRange()) {
                target = null; // Give up
            }
        }
    }

    private void tryToShoot() {
        float distanceBetweenOriginAndShootPosition = (float) MathHelper.getDistanceBetweenPoints(
                position.x,
                position.y,
                position.x + relativeShootPosition.x,
                position.y + relativeShootPosition.y);
        float angleBetweenOriginAndShootPosition = (float) MathHelper.getAngleBetweenPointsInRadians(
                position.x,
                position.y,
                position.x + relativeShootPosition.x,
                position.y + relativeShootPosition.y);

        Vector2 spawnPoint = new Vector2( // TODO Close, but not right
                (float) (position.x + Math.cos(getAngleInRadians()
                        + angleBetweenOriginAndShootPosition) * distanceBetweenOriginAndShootPosition),
                (float) (position.y + Math.sin(getAngleInRadians()
                        + angleBetweenOriginAndShootPosition) * distanceBetweenOriginAndShootPosition));
        AbstractAmmunition ammunition = weapon.tryToShoot(worldController, spawnPoint, angleDeg);

        // TODO Decrease accuracy? Use SimTick as hash?

        if (ammunition != null) { // Weapon was fired
            worldController.getAmmunitionContainer().add(ammunition);

            GeneralMuzzleFire muzzleFire = new GeneralMuzzleFire(worldController, spawnPoint, angleDeg);
            worldController.getEffectsContainer().add(muzzleFire);

            if (!worldController.isPredictedWorld()) { // Play sound in real world
                worldController.getAudioEffectContainer().add(
                        new AudioEffect(
                                owner.getWorldController(),
                                SoundContainer.getInstance().getSound("m4"),
                                0.08f,
                                new Vector2(position.x, position.y)));
            }
        }
    }

    private void findNewClosestTarget() {
        // Can return null so the target is also be null if nothing is found.
        target = findClosestEnemyInRange();
    }

    protected AbstractUnit findClosestEnemyInRange() {
        // Find all units in range
        ArrayList<AbstractUnit> targetsInRange = new ArrayList<AbstractUnit>();
        for (AbstractUnit unit : owner.getWorldController().getUnitContainerAllUnits().getUnits()) {
            if (unit.getTeam() == owner.getTeam()) {
                continue;
            }

            if (MathHelper.getDistanceBetweenPoints(position.x, position.y, unit.getX(), unit.getY()) <= range) {
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
                    position.x,
                    position.y,
                    unit.getX(),
                    unit.getY()) <
                    MathHelper.getDistanceBetweenPoints(
                            position.x,
                            position.y,
                            currentClosestTarget.getX(),
                            currentClosestTarget.getY())) {
                currentClosestTarget = unit;
            }
        }

        return currentClosestTarget;
    }

    private boolean hasTarget() {
        return target != null;
    }

    @Override
    protected void initializeDimensions() {
        // Can be left null.
    }

    @Override
    protected void initializeCollisionMask() {
        // Can be left null.
    }

    @Override
    protected void initializeMovement() {
        // Can be left null.
    }

    @Override
    protected void updateCollisionMask() {
        // Can be left null.
    }

    @Override
    public boolean onCollision(Vector2 point) {
        return false;
    }

    public boolean isTargetInRange() {
        if (hasTarget()) {
            return MathHelper.getDistanceBetweenPoints(position.x, position.y, target.getX(), target.getY()) <= range;
        }

        return false;
    }

    public boolean turretIsRotatedTowardsTarget() {
        double angleBetweenTurretAndTargetInRadians = MathHelper.getAngleBetweenPointsInRadians(
                position.x,
                position.y,
                target.getPosition().x,
                target.getPosition().y);

        return MathHelper.round(getAngle(), 0) == MathHelper.round(Math.toDegrees(angleBetweenTurretAndTargetInRadians), 0);
    }

    /** Return true if there are now obstacles between the turret and the target.
     * @param accuracy How many pixels the dot will be moved per loop.
     * Zero means very accurate and consumes lots of time. Max value is 200.
     */
     // TODO There is a faster way to implement this:
     // http://code.tutsplus.com/tutorials/quick-tip-collision-detection-between-a-circle-and-a-line-segment--active-10632
     // https://www.google.fi/?gws_rd=ssl#q=%22line+rectangle+collision+detection

    public boolean isTargetInSight(int accuracy) {
        if (accuracy > 200) {
            accuracy = 200;
        }

        /* TODO This is used mainly for humans. Tanks' turret is higher than humans so humans are not
         * in the line of sight. This method needs to improved when tanks and other units are added in to the game. */

        // Create a dot
        Vector2 checkSight = new Vector2(position);
        double angleBetweenDotAndTargetRad = MathHelper.getAngleBetweenPointsInRadians(
                checkSight.x,
                checkSight.y,
                target.getX(),
                target.getY());

        boolean isCollisionDetected;
        checkSightLoop:
        while (true) {
            // Move the dot towards the target.
            checkSight.x = (float) (checkSight.x + Math.cos(angleBetweenDotAndTargetRad) * accuracy);
            checkSight.y = (float) (checkSight.y + Math.sin(angleBetweenDotAndTargetRad) * accuracy);

            // Check if there is a collision between the dot and some obstacle.
            for (AbstractUnit unit : worldController.getUnitContainerAllUnits().getUnits()) {
                if (unit == owner || unit == target) {
                    continue;
                }

                if (unit.onCollision(checkSight)) {
                    isCollisionDetected = true;
                    break checkSightLoop;
                }
            }

            // TODO Check also other obstacles like buildings, trees, rocks etc.

            // Target position reached
            if (MathHelper.getDistanceBetweenPoints(checkSight.x, checkSight.y, target.getX(), target.getY()) <= accuracy) {
                isCollisionDetected = false;
                break;
            }
        }

        return !isCollisionDetected;
    }

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(Vector2 relativePosition) {
        this.relativePosition = relativePosition;
    }

    public void setRelativeShootPosition(Vector2 relativePosition) {
        this.relativeShootPosition = relativePosition;
    }

    public void setOwner(final AbstractUnit owner) {
        this.owner = owner;
    }

    public void setWeapon(final AbstractWeapon weapon) {
        this.weapon = weapon;
    }
}
