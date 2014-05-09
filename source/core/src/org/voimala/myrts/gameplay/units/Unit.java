package org.voimala.myrts.gameplay.units;

public class Unit {

    private float x = 0;
    private float y = 0;
    private float angle = 0;
    private double velocity = 0;
    private double acceleration = 0;
    private double deceleration = 0;
    private UnitType type;

    public Unit() {

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

    public void setPosition(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        if (angle < 0) {
            angle = 0;
        }

        if (angle > 360) {
            angle = 360;
        }

        this.angle = angle;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(final double velocity) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(final double deceleration) {
        this.deceleration = deceleration;
    }

    public void update(float deltaTime) {
    }
}
