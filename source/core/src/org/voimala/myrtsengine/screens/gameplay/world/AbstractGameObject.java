package org.voimala.myrtsengine.screens.gameplay.world;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.movements.AbstractMovement;

;

public abstract class AbstractGameObject implements Cloneable {

    protected Object collisionMask;
    protected AbstractMovement movement = null;
    protected long ObjectId; // Every object should have an unique id
    protected Vector2 position = new Vector2(0, 0);
    protected float angle = 0; // 0 = right, 90 = top, 180 = left, 270 = down. Always between 0 and 360 (inclusive)
    protected float width = 0;
    protected float height = 0;
    private static long nextFreeId = 0;

    public AbstractGameObject clone() throws CloneNotSupportedException {
        AbstractGameObject gameObjectClone = (AbstractGameObject) super.clone();
        Vector2 positionClone = new Vector2(position.x, position.y);
        gameObjectClone.setPosition(positionClone);

        return gameObjectClone;
    }

    public AbstractGameObject(final long id) {
        initialize(id);
    }

    private void initialize(final long id) {
        initializeId(id);
        initializeDimensions();
        initializeCollisionMask();
        initializeMovement();
    }

    private void initializeId(final long id) {
        this.ObjectId = id;
    }

    protected abstract void initializeDimensions();
    protected abstract void initializeCollisionMask();
    protected abstract void initializeMovement();

    public void setMovement(final AbstractMovement movement) {
        this.movement = movement;
    }

    public AbstractMovement getMovement() {
        return movement;
    }

    public void update(final float deltaTime) {
        updateMovement(deltaTime);
        updateCollisionMask();
    }

    private void updateMovement(float deltaTime) {
        if (movement != null) {
            movement.update(deltaTime);
        }
    }

    /** Updates the current position / size of the collision mask. Called on every world update. */
    protected abstract void updateCollisionMask();

    public static long getNextFreeId() {
        return nextFreeId++;
    }

    public static void resetNextFreeId() {
        nextFreeId = 0;
    }

    public float getX() {
        return position.x;
    }

    public void setX(final float x) {
        position.x = x;
    }

    public float getY() {
        return position.y;
    }

    public void setY(final float y) {
        position.y = y;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(final float angle) {
        this.angle = angle;
        keepAngleValueInRange();
    }

    public void rotate(final float angle) {
        this.angle += angle;
        keepAngleValueInRange();
    }

    public double getAngleInRadians() {
        return Math.toRadians(angle);
    }

    private void keepAngleValueInRange() {
        if (angle < 0) {
            this.angle = 360 - Math.abs(angle);
        }

        if (angle > 360) {
            this.angle = 0 + angle - 360;
        }
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(final float width) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be equal or greater than 0.");
        }

        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(final float height) {
        if (height < 0) {
            throw new IllegalArgumentException("height must be equal or greater than 0.");
        }

        this.height = height;
    }

    /** @return Is the given point inside this object's collission mask */
    public abstract boolean onCollision(Vector2 point);

    public long getObjectId() {
        return ObjectId;
    }

    /** Adds the given value to unit's x value. */
    public void moveX(final double x) {
        position.x += x;
    }

    /** Adds the given value to unit's y value. */
    public void moveY(final double y) {
        position.y += y;
    }
}
