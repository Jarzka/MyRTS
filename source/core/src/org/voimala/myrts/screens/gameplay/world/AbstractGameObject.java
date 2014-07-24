package org.voimala.myrts.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.movements.AbstractMovement;

;

public abstract class AbstractGameObject implements Cloneable {

    private static final String TAG = AbstractGameObject.class.getName();

    protected WorldController worldController;
    protected AbstractMovement movement = null;
    protected long ObjectId; // Every object should have an unique id
    protected Vector2 position = new Vector2(0, 0); /* Object's position in 2D space. Origin at center. */
    protected float angleDeg = 0; // 0 = right, 90 = top, 180 = left, 270 = down. Always between 0 and 360 (inclusive)
    protected float width = 0;
    protected float height = 0;
    protected Object collisionMask;
    protected Sprite sprite;

    public AbstractGameObject(final WorldController worldController) {
        this.worldController = worldController;
        initialize();
    }

    /** By default the cloned object's worldController will point to the same world. */
    public AbstractGameObject clone() throws CloneNotSupportedException {
        AbstractGameObject gameObjectClone = (AbstractGameObject) super.clone();

        Vector2 positionClone = new Vector2(position.x, position.y);
        gameObjectClone.setPosition(positionClone);

        if (movement != null) {
            AbstractMovement movementClone = movement.clone();
            movementClone.setOwner(gameObjectClone);
            gameObjectClone.setMovement(movementClone);
        }

        // TODO Clone collision mask

        return gameObjectClone;
    }

    private void initialize() {
        initializeId();
        initializeDimensions();
        initializeCollisionMask();
        initializeMovement();
    }

    protected void initializeId() {
        this.ObjectId = worldController.getNextFreeId();
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

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setPosition(Vector2 position) {
        if (this.position.x != position.x || this.position.y != position.y) {
            this.position = new Vector2(position.x, position.y);
            //Gdx.app.debug(TAG, "Object id: " + getObjectId() + " new position. x: " + position.x + ". y: " + position.y);
        }

    }

    public Vector2 getPosition() {
        return position;
    }

    /** Returns the angle in degrees. */
    public float getAngle() {
        return angleDeg;
    }

    public void setAngle(final float angleDeg) {
        this.angleDeg = angleDeg;
        keepAngleValueInRange();
        //Gdx.app.debug(TAG, "Object id: " + getObjectId() + " new angle: " + this.angleDeg);
    }

    public void rotate(final float angle) {
        if (angle != 0) {
            this.angleDeg += angle;
            keepAngleValueInRange();
            //Gdx.app.debug(TAG, "Object id: " + getObjectId() + " new angle: " + this.angleDeg);
        }
    }

    public double getAngleInRadians() {
        return Math.toRadians(angleDeg);
    }

    private void keepAngleValueInRange() {
        if (angleDeg < 0) {
            this.angleDeg = 360 - Math.abs(angleDeg);
        }

        if (angleDeg > 360) {
            this.angleDeg = 0 + angleDeg - 360;
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

    public void setWorldController(final WorldController worldController) {
        this.worldController = worldController;
    }

    public WorldController getWorldController() {
        return worldController;
    }
}
