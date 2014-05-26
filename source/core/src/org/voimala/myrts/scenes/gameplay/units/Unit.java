package org.voimala.myrts.scenes.gameplay.units;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.exceptions.GameLogicException;
import org.voimala.myrts.scenes.gameplay.GameObject;
import org.voimala.myrts.scenes.gameplay.units.movements.Movement;

public abstract class Unit extends GameObject {

    protected Object collisionMask;
    protected int player = 0;
    protected int team = 0;
    protected Movement movement = null;
    protected UnitType type;

    private boolean isSelected = false;
    private String id;

    public Unit(final String id) {
        initialize(id);
    }

    private void initialize(final String id) {
        initializeId(id);
        initializeDimensions();
        initializeCollisionMask();
        initializeMovement();
    }

    @Override
    public Unit clone() throws CloneNotSupportedException {
        Unit unitClone = (Unit) super.clone();
        Movement movementClone = movement.clone();
        movementClone.setOwner(unitClone);
        // Object collisionMaskClone = collisionMask.clone(); TODO CLONE COLLISION MASK
        unitClone.setMovement(movementClone);

        return unitClone;
    }

    protected abstract void initializeDimensions();
    protected abstract void initializeCollisionMask();
    protected abstract void initializeMovement();

    private void initializeId(final String id) {
        this.ObjectId = id;
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

    /** Adds the given value to unit's x value. */
    public void moveX(final double x) {
        this.x += x;
    }

    /** Adds the given value to unit's y value. */
    public void moveY(final double y) {
        this.y += y;
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
}
