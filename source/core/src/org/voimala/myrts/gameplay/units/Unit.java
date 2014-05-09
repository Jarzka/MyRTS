package org.voimala.myrts.gameplay.units;

public class Unit {

    private double x = 0;
    private double y = 0;
    private double angle = 0;
    private double velocity = 0;
    private double acceleration = 0;
    private double deceleration = 0;
    private UnitType type;

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
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

}
