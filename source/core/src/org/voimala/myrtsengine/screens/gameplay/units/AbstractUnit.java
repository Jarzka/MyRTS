package org.voimala.myrtsengine.screens.gameplay.units;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.exceptions.GameLogicException;
import org.voimala.myrtsengine.movements.AbstractMovement;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.units.turrets.AbstractTurret;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnit extends AbstractGameObject {

    protected int player = 0;
    protected int team = 0;
    protected UnitType type;
    protected boolean isSelected = false;
    protected ArrayList<AbstractTurret> turrets = new ArrayList<AbstractTurret>();

    public AbstractUnit(final WorldController worldController) {
        super(worldController);
        initializeTurrets();
    }

    protected abstract void initializeTurrets();

    @Override
    public AbstractUnit clone() throws CloneNotSupportedException {
        AbstractUnit unitClone = (AbstractUnit) super.clone();

        AbstractMovement movementClone = movement.clone();
        movementClone.setOwner(unitClone);
        unitClone.setMovement(movementClone);

        // Clone turrets
        ArrayList<AbstractTurret> turretsClone = new ArrayList<AbstractTurret>();
        for (AbstractTurret turret : turrets) {
            AbstractTurret turretClone = turret.clone();
            turretClone.setOwner(unitClone);
            turretsClone.add(turretClone);
        }
        unitClone.setTurrets(turretsClone);

        // Object collisionMaskClone = collisionMask.clone(); TODO CLONE COLLISION MASK

        return unitClone;
    }

    private void setTurrets(final ArrayList<AbstractTurret> turrets) {
        this.turrets = turrets;
    }

    @Override
    public void updateState(final float deltaTime) {
        super.updateState(deltaTime);
        updateTurretState(deltaTime);
    }

    private void updateTurretState(final float deltaTime) {
        for (AbstractTurret turret : turrets) {
            turret.updateState(deltaTime);
        }
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

    public WorldController getWorldController() {
        return worldController;
    }

    public List<AbstractTurret> getTurrets() {
        return turrets;
    }
}
