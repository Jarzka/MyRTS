package org.voimala.myrts.gameplay.units.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.gameplay.units.Unit;
import org.voimala.utility.MathHelper;

public abstract class Movement {
    protected double currentVelocity = 0; /** px/s */
    protected double maxVelocity = 0; /** px/s */
    protected double acceleration = 0; /** px/s */
    protected double deceleration = 0; /** px/s */
    protected double maxRotationVelocity = 0; /** px/s */
    protected double currentRotationVelocity = 0; /** px/s */
    protected double rotationAcceleration = 0; /** px/s */ // TODO
    protected double rotationDeceleration = 0; /** px/s */ // TODO
    protected Unit ownerUnit = null;

    private void checkMaxRotationVelocity(double maxRotationVelocity) {
        if (maxRotationVelocity < 0) {
            throw new GameLogicException("Rotation speed must be equal or greater than 0.");
        }
    }

    private void checkDeceleration(double rotationDeceleration) {
        if (rotationDeceleration < 0) {
            throw new GameLogicException("Deceleration must be equal or greater than 0.");
        }
    }

    private void checkAcceleration(double rotationAcceleration) {
        if (rotationAcceleration < 0) {
            throw new GameLogicException("Acceleration must be equal or greater than 0.");
        }
    }

    private void checkRotationAcceleration(double acceleration) {
        if (acceleration < 0) {
            throw new GameLogicException("Rotation acceleration must be equal or greater than 0.");
        }
    }

    private void checkRotationDeceleration(double deceleration) {
        if (acceleration < 0) {
            throw new GameLogicException("Rotation deceleration must be equal or greater than 0.");
        }
    }

    private void checkMaxVelocity(double maxVelocity) {
        if (maxVelocity < 0) {
            throw new GameLogicException("Max velocity must be equal or greater than 0.");
        }
    }

    public Movement(final Unit ownerUnit) {
        this.ownerUnit = ownerUnit;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(final double maxVelocity) {
        checkMaxVelocity(maxVelocity);
        this.maxVelocity = maxVelocity;
    }

    public double getMaxRotationVelocity() {
        return maxRotationVelocity;
    }

    public void setMaxRotationVelocity(final double rotationVelocity) {
        checkMaxRotationVelocity(rotationVelocity);
        this.maxRotationVelocity = rotationVelocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(final double acceleration) {
        checkAcceleration(acceleration);
        this.acceleration = acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(final double deceleration) {
        checkDeceleration(deceleration);
        this.deceleration = deceleration;
    }

    public abstract void update(final float deltaTime);

    protected boolean hasReachedPoint(Vector2 point) {
        return MathHelper.getDistanceBetweenPoints(ownerUnit.getX(),
                ownerUnit.getY(),
                point.x,
                point.y) <= 8;
    }

    public double getRotationAcceleration() {
        return rotationAcceleration;
    }

    public void setRotationAcceleration(final double rotationAcceleration) {
        checkRotationAcceleration(rotationAcceleration);
        this.rotationAcceleration = rotationAcceleration;
    }

    public double getRotationDeceleration() {
        return rotationDeceleration;
    }

    public void setRotationDeceleration(final double rotationDeceleration) {
        checkRotationDeceleration(rotationDeceleration);
        this.rotationDeceleration = rotationDeceleration;
    }

}
