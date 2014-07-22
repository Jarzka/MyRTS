package org.voimala.myrtsengine.screens.gameplay.world;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.movements.AbstractMovement;

;

public abstract class AbstractGameObject implements Cloneable {

    protected WorldController worldController;
    protected AbstractMovement movement = null;
    protected long ObjectId; // Every object should have an unique id
    protected Vector2 position = new Vector2(0, 0); /* Object's position in 2D space. Origin at center. */
    protected float angle = 0; // 0 = right, 90 = top, 180 = left, 270 = down. Always between 0 and 360 (inclusive)
    protected float width = 0;
    protected float height = 0;
    protected Object collisionMask;
    protected static long nextFreeId = 0;
    protected Sprite sprite;

    public AbstractGameObject clone() throws CloneNotSupportedException {
        AbstractGameObject gameObjectClone = (AbstractGameObject) super.clone();
        Vector2 positionClone = new Vector2(position.x, position.y);
        gameObjectClone.setPosition(positionClone);

        return gameObjectClone;
    }

    public AbstractGameObject(final WorldController worldController) {
        this.worldController = worldController;
        initialize();
    }

    private void initialize() {
        initializeId();
        initializeDimensions();
        initializeCollisionMask();
        initializeMovement();
    }

    private void initializeId() {
        this.ObjectId = getNextFreeId();
    }

    protected abstract void initializeDimensions();
    protected abstract void initializeCollisionMask();
    protected abstract void initializeMovement();

    /** Should return a sprite that corresponds the object's current state */
    public abstract Sprite getSprite();

    public void setMovement(final AbstractMovement movement) {
        this.movement = movement;
    }

    public AbstractMovement getMovement() {
        return movement;
    }

    public void updateState(final float deltaTime) {
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

    private static long getNextFreeId() {
        return nextFreeId++;
    }

    public static void resetNextFreeId() {
        nextFreeId = 0;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
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

}
