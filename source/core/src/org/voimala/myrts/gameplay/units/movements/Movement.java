package org.voimala.myrts.gameplay.units.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.gameplay.units.Unit;

public abstract class Movement {
    protected double velocity = 0;
    protected double acceleration = 0;
    protected double deceleration = 0;
    protected double rotationVelocity = 0;
    protected double rotationAcceleration = 0; // TODO
    protected double rotationDeceleration = 0; // TODO
    protected Unit ownerUnit = null;

    public Movement(final Unit ownerUnit,
                    final double acceleration,
                    final double deceleration,
                    final double rotationVelocity) {
        if (acceleration < 0) {
            throw new GameLogicException("Acceleration must be equal or greater than 0.");
        }

        if (deceleration < 0) {
            throw new GameLogicException("Deceleration must be equal or greater than 0.");
        }

        if (rotationVelocity < 0) {
            throw new GameLogicException("Rotation speed must be equal or greater than 0.");
        }

        this.ownerUnit = ownerUnit;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.rotationVelocity = rotationVelocity;
    }

    public Movement(final Unit ownerUnit) {
        this.ownerUnit = ownerUnit;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(final double velocity) {
        if (velocity < 0) {
            throw new GameLogicException("Velocity must be equal or greater than 0.");
        }

        this.velocity = velocity;
    }

    public double getRotationSpeed() {
        return rotationVelocity;
    }

    public void setRotationSpeed(final double rotationVelocity) {
        if (rotationVelocity < 0) {
            throw new GameLogicException("Rotation speed must be equal or greater than 0.");
        }

        this.rotationVelocity = rotationVelocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(final double acceleration) {
        if (acceleration < 0) {
            throw new GameLogicException("Acceleration must be equal or greater than 0.");
        }

        this.acceleration = acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(final double deceleration) {
        if (deceleration < 0) {
            throw new GameLogicException("Deceleration must be equal or greater than 0.");
        }

        this.deceleration = deceleration;
    }

    public abstract void update(final float deltaTime);

    protected boolean hasReachedPoint(Vector2 nextPoint) {
        return ownerUnit.getX() == nextPoint.x
                && ownerUnit.getY() == nextPoint.y;
    }

}
