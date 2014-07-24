package org.voimala.myrts.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.screens.gameplay.world.AbstractGameObject;
import org.voimala.utility.MathHelper;
import org.voimala.utility.RotationDirection;

import java.util.ArrayList;

public abstract class AbstractMovement implements Cloneable {
    protected double currentVelocity = 0; /// px/s
    protected double maxVelocity = 0; /// px/s
    protected double acceleration = 0; /// px/s
    protected double deceleration = 0; /// px/s

    protected double currentRotationVelocity = 0; /// deg/s
    protected double maxRotationVelocity = 0; /// deg/s
    protected double rotationAcceleration = 0; /// deg/s
    protected double rotationDeceleration = 0; /// deg/s
    protected RotationDirection currentRotationDirection;

    protected AbstractGameObject owner = null;
    protected ArrayList<Vector2> pathPoints = new ArrayList<Vector2>();

    public AbstractMovement(final AbstractGameObject owner) {
        this.owner = owner;
    }

    private void checkMaxRotationVelocity(double maxRotationVelocity) {
        if (maxRotationVelocity < 0) {
            throw new GameLogicException("Rotation speed must be equal or greater than 0.");
        }
    }

    @Override
    /** The owner unit of the movement is the same as the original owner.  */
    public AbstractMovement clone() throws CloneNotSupportedException {
        AbstractMovement movementClone = (AbstractMovement) super.clone();

        ArrayList<Vector2> pathPointsClone = clonePathPoints();
        movementClone.setPathPoints(pathPointsClone);

        return movementClone;
    }

    protected ArrayList<Vector2> clonePathPoints() {
        ArrayList<Vector2> pathPointsClone = new ArrayList<Vector2>();

        for (Vector2 point : pathPoints) {
            Vector2 pointClone = new Vector2();
            pointClone.x = point.x;
            pointClone.y = point.y;
            pathPointsClone.add(pointClone);
        }

        return pathPointsClone;
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
        return MathHelper.getDistanceBetweenPoints(owner.getX(),
                owner.getY(),
                point.x,
                point.y) <= 8;
    }

    public boolean hasReachedDestination() {
        if (pathPoints.isEmpty()) {
            return true;
        }

        return false;
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

    public void addPathPoint(final Vector2 point) {
        pathPoints.add(point);
    }

    public void setSinglePathPoint(final Vector2 point) {
        pathPoints.clear();
        pathPoints.add(point);
    }

    public ArrayList<Vector2> getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(ArrayList<Vector2> pathPoints) {
        this.pathPoints = pathPoints;
    }

    public void setOwner(AbstractGameObject owner) {
        this.owner = owner;
    }

    public void setVelocity(final double velocity) {
        this.currentVelocity = velocity;
    }

    public String getStateHash() {
        String hash = "";
        hash += owner.getPosition().x + owner.getPosition().y + owner.getAngle();
        return hash;
    }
}
