package org.voimala.myrts.gameplay.units;

import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.gameplay.units.movements.Movement;

public class Unit {

    private float x = 0;
    private float y = 0;
    private float angle = 0; // 0 = top, 90 = left, 180 = down, 270 = right
    private int player = 0;
    private int team = 0;
    protected Movement movement = null;
    private UnitType type;

    public Unit() {
        initialize();
    }

    private void initialize() {
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

    public void setPosition(final int x, final int y) {
        setX(x);
        setY(y);
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

    public void update(final float deltaTime) {
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
    }
}
