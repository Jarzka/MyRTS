package org.voimala.myrts.screens.gameplay.weapons;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public abstract class AbstractWeapon implements Cloneable {

    protected float reloadState;
    protected float reloadTimeSeconds;

    protected float shootState;
    protected float shootTimeSeconds; /// How much time does it take to fire the weapon once

    protected int shootTimes; /// How many times the weapon is allowed to fire before it has to be reloaded
    protected int shotsFired; /// Is used to calculate number of shots before reload.

    protected WeaponOptions weaponOptions = new WeaponOptions();

    protected WeaponState weaponState = WeaponState.IDLE;

    public AbstractWeapon() {
        initialize();
    }

    public AbstractWeapon clone() throws CloneNotSupportedException {
        return (AbstractWeapon) super.clone();
    }

    public void updateState(final float deltaTime) {
        if (weaponState == WeaponState.FIRING) {
            shootState += deltaTime;
            checkIfShotHasEnded();
        } else if (weaponState == WeaponState.RELOADING) {
            reloadState += deltaTime;
            checkIfReloadIsFinished();
        }
    }

    private void checkIfShotHasEnded() {
        if (shootState >= shootTimeSeconds) {
            shootState = 0;

            // Time to reload the weapon?
            if (shotsFired >= shootTimes) {
                shotsFired = 0;
                weaponState = WeaponState.RELOADING;
            } else {
                weaponState = WeaponState.IDLE;
            }
        }
    }

    private void checkIfReloadIsFinished() {
        if (reloadState >= reloadTimeSeconds) {
            reloadState = 0;
            weaponState = WeaponState.IDLE;
        }
    }

    public WeaponState getState() {
        return weaponState;
    }

    protected abstract void initialize();

    /**
     * @param position The point where to spawn the shot
     * @param angle The target angle for the shot
     * @return The shot object (bullet, missile etc.)
     */
    public abstract AbstractAmmunition tryToShoot(
            final WorldController worldController,
            final Vector2 position,
            final float angle,
            WeaponOptions weaponOptions);


    public WeaponOptions getWeaponOptions() {
        return weaponOptions;
    }
}
