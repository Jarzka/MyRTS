package org.voimala.myrts.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrts.screens.gameplay.world.AbstractGameObject;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

import java.util.ArrayList;

public abstract class AbstractTurret extends AbstractGameObject implements Cloneable {

    protected AbstractUnit ownerUnit;
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

    protected AbstractTurretState turretState = new TurretStateIdle(this);

    protected long range = 100;

    public AbstractTurret(AbstractUnit ownerUnit, AbstractWeapon weapon) {
        super(ownerUnit.getWorldController());
        this.ownerUnit = ownerUnit;
        this.position = new Vector2(ownerUnit.getX(), ownerUnit.getY());
        this.angleDeg = ownerUnit.getAngle();
        this.weapon = weapon;
    }

    /**
     * By default the clone will have the same owner as the original object. Target will be set to null.
     * If the entire world is cloned, it should be possible to find the corresponding target in the cloned world.
     */
    public AbstractTurret clone() throws CloneNotSupportedException {
        AbstractTurret turretClone = (AbstractTurret) super.clone();

        AbstractWeapon weaponClone = weapon.clone();
        turretClone.setWeapon(weaponClone);

        AbstractTurretState turretStateClone = turretState.clone();
        turretStateClone.setOwnerTurret(turretClone);
        turretClone.setState(turretStateClone);

        turretClone.setRelativePosition(new Vector2(relativePosition.x, relativePosition.y));
        turretClone.setRelativeShootPosition(new Vector2(relativeShootPosition.x, relativeShootPosition.y));

        turretClone.setTarget(null);

        return turretClone;
    }

    public void setState(final AbstractTurretState turretState) {
        this.turretState = turretState;
    }

    public void updateState(final float deltaTime) {
        super.updateState(deltaTime);
        updateTurretState(deltaTime);
        updateWeaponState(deltaTime);
    }

    private void updateTurretState(final float deltaTime) {
        updatePosition();
        updateRotation(deltaTime);

        turretState.updateState(deltaTime);
    }

    protected void updatePosition() {
        position.x = ownerUnit.getX() + relativePosition.x;
        position.y = ownerUnit.getY() + relativePosition.y;
    }

    private void updateWeaponState(final float deltaTime) {
        if (weapon != null) {
            weapon.updateState(deltaTime);
        }
    }

    private void updateRotation(final float deltaTime) {
        handlePhysicalRotation(deltaTime);
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

    public boolean hasTarget() {
        return target != null;
    }

    @Override
    protected void initializeDimensions() {
        // Can be left empty.
    }

    @Override
    protected void initializeCollisionMask() {
        // Can be left empty.
    }

    @Override
    protected void initializeMovement() {
        // Can be left empty.
    }

    @Override
    protected void updateCollisionMask() {
        // Can be left empty.
    }

    @Override
    public boolean onCollision(Vector2 point) {
        return false;
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

    public void setOwnerUnit(final AbstractUnit ownerUnit) {
        this.ownerUnit = ownerUnit;
    }

    public void setWeapon(final AbstractWeapon weapon) {
        this.weapon = weapon;
    }

    public double getSteeringWheel() {
        return steeringWheel;
    }

    public void setSteeringWheel(final double steeringWheel) {
        this.steeringWheel = steeringWheel;
    }

    public void setTarget(final AbstractUnit target) {
        this.target = target;
    }

    public AbstractUnit getTarget() {
        return target;
    }

    public AbstractUnit getOwnerUnit() {
        return ownerUnit;
    }

    public Vector2 getRelativeShootPosition() {
        return relativeShootPosition;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public long getRange() {
        return range;
    }

    public double getCurrentRotationVelocity() {
        return currentRotationVelocity;
    }

    public double getRotationDeceleration() {
        return rotationDeceleration;
    }

    public void findNewClosestTarget() {
        // Target is null if nothing is found.
        setTarget(findClosestEnemyInRange());
    }

    // TODO This is still one of the slowest methods to execute in this app.
    protected AbstractUnit findClosestEnemyInRange() {
        // Find all units in range that are not in the same team as this turret's owner unit.
        ArrayList<AbstractUnit> targetsInRange = new ArrayList<AbstractUnit>();
        for (int i = 0; i <= 8; i++) {
            if (i == ownerUnit.getTeam()) {
                continue;
            }

            for (AbstractUnit unit : getWorldController().getUnitContainer().findUnitsByTeam(i)) {
                if (unit.getTeam() == getOwnerUnit().getTeam()) {
                    continue;
                }

                if (MathHelper.getDistanceBetweenPoints(getPosition().x,
                        getPosition().y,
                        unit.getX(),
                        unit.getY()) <= getRange()) {
                    targetsInRange.add(unit);
                }
            }
        }


        // Find the closest one

        AbstractUnit currentClosestTarget = null;
        if (!targetsInRange.isEmpty()) {
            currentClosestTarget = targetsInRange.get(0);
        }

        for (AbstractUnit unit : targetsInRange) {
            if (MathHelper.getDistanceBetweenPoints(
                    getPosition().x,
                    getPosition().y,
                    unit.getX(),
                    unit.getY()) <
                    MathHelper.getDistanceBetweenPoints(
                            getPosition().x,
                            getPosition().y,
                            currentClosestTarget.getX(),
                            currentClosestTarget.getY())) {
                currentClosestTarget = unit;
            }
        }

        return currentClosestTarget;
    }
}
