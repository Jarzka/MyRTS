package org.voimala.myrts.gameplay.units;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.states.UnitMovementState;
import org.voimala.myrts.gameplay.units.states.UnitMovementStateStopped;

import java.util.ArrayList;

public class Unit {

    private float x = 0;
    private float y = 0;
    private float angle = 0;
    private double velocity = 0;
    private double acceleration = 0;
    private double deceleration = 0;
    private ArrayList<Vector2> pathPoints = new ArrayList<>();
    private UnitType type;
    private int player = 0;
    private int team = 0;
    private UnitMovementState movementState = new UnitMovementStateStopped(this);

    public Unit() {
        initialize();
    }

    private void initialize() {
        // TODO Add test path points
        pathPoints.add(new Vector2(256, 256));
        pathPoints.add(new Vector2(512, 256));
        pathPoints.add(new Vector2(512, 512));
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
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

    public void update(final float deltaTime) {
        movementState.update();
    }

    private void move() {

    }

    public void addPathPoint(Vector2 point) {
        pathPoints.add(point);
    }

    public void clearPathPoints() {
        pathPoints.clear();
    }

    public ArrayList<Vector2> getPathPoints() {
        return pathPoints;
    }

    public void changeMovementState(UnitMovementState state) {
        this.movementState = state;
    }
}
