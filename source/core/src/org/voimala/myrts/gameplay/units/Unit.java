package org.voimala.myrts.gameplay.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.gameplay.units.movements.Movement;

public abstract class Unit {

    protected float x = 0;
    protected float y = 0;
    protected float angle = 0; // 0 = right, 90 = top, 180 = left, 270 = down. Always between 0 and 360 (inclusive)
    protected int player = 0;
    protected int team = 0;
    protected Movement movement = null;
    protected  UnitType type;
    protected float width = 0;
    protected float height = 0;
    protected Object collisionMask;

    private boolean isSelected = false;

    public Unit() {
        initialize();
    }

    protected void initialize() {
        initializeDimensions();
        initializeCollisionMask();
        initializeMovement();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(final int player) {
        if (player < 0) {
            throw new GameLogicException("Player number must be equal or greater than 0");
        }

        this.player = player;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(final int team) {
        if (team < 0) {
            throw new GameLogicException("Team number must be equal or greater than 0");
        }

        this.team = team;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }

    /** Adds the given value to unit's x value. */
    public void moveX(final double x) {
        this.x += x;
    }

    /** Adds the given value to unit's y value. */
    public void moveY(final double y) {
        this.y += y;
    }

    public void setPosition(final int x, final int y) {
        setX(x);
        setY(y);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(final float angle) {
        this.angle = angle;
        keepAngleValueInRange();
    }

    private void keepAngleValueInRange() {
        if (angle < 0) {
            float newAngle = 360 - Math.abs(angle);
            this.angle = newAngle;
        }

        if (angle > 360) {
            float newAngle = 0 + angle - 360;
            this.angle = newAngle;
        }
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

    public void setMovement(final Movement movement) {
        this.movement = movement;
    }

    public Movement getMovement() {
        return movement;
    }

    public void rotate(final float angle) {
        this.angle += angle;
        keepAngleValueInRange();
    }

    public double getAngleInRadians() {
        return Math.toRadians(angle);
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

    protected abstract void updateCollisionMask();
    public abstract boolean onCollision(Vector2 point);
    protected abstract void initializeDimensions();
    protected abstract void initializeCollisionMask();
    protected abstract void initializeMovement();
}
