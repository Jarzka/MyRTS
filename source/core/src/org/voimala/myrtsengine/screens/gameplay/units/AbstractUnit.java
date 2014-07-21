package org.voimala.myrtsengine.screens.gameplay.units;

import org.voimala.myrtsengine.exceptions.GameLogicException;
import org.voimala.myrtsengine.movements.AbstractMovement;
import org.voimala.myrtsengine.screens.gameplay.units.turrets.Turret;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;

import java.util.ArrayList;

public abstract class AbstractUnit extends AbstractGameObject {

    protected int player = 0;
    protected int team = 0;
    protected UnitType type;
    protected boolean isSelected = false;
    protected ArrayList<Turret> turrets = new ArrayList<Turret>();

    public AbstractUnit(final long id) {
        super(id);
        initializeTurrets();
    }

    protected abstract void initializeTurrets();

    @Override
    public AbstractUnit clone() throws CloneNotSupportedException {
        AbstractUnit unitClone = (AbstractUnit) super.clone();
        AbstractMovement movementClone = movement.clone();
        movementClone.setOwner(unitClone);
        // Object collisionMaskClone = collisionMask.clone(); TODO CLONE COLLISION MASK
        unitClone.setMovement(movementClone);

        return unitClone;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getPlayerNumber() {
        return player;
    }

    public void setPlayerNumber(final int player) {
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

}
