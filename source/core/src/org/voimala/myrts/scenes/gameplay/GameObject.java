package org.voimala.myrts.scenes.gameplay;

import com.badlogic.gdx.math.Vector2;

public abstract class GameObject implements Cloneable {

    protected String ObjectId; // Every unit should have an unique id
    protected float x = 0;
    protected float y = 0;
    protected float angle = 0; // 0 = right, 90 = top, 180 = left, 270 = down. Always between 0 and 360 (inclusive)
    protected float width = 0;
    protected float height = 0;

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

    public void rotate(final float angle) {
        this.angle += angle;
        keepAngleValueInRange();
    }

    public double getAngleInRadians() {
        return Math.toRadians(angle);
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

    public String getObjectId() {
        return ObjectId;
    }
}
