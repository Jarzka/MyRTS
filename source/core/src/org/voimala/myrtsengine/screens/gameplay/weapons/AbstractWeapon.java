package org.voimala.myrtsengine.screens.gameplay.weapons;

public abstract class AbstractWeapon {

    protected long reloadTimeMs; // How much time does it take to reload the weapon in milliseconds
    protected long shootTimeMs; // How much time does it take to shoot once.
    protected int shootTimes; // How many times the weapon is allowed to be fired before it has to be reloaded

    protected long reloadStartedTimestamp;
    protected long lastShotTimestamp;
    protected int shotsFired;

    protected WeaponState weaponState = WeaponState.IDLE;

    public AbstractWeapon() {
        initialize();
    }

    public void updateState() {
        if (weaponState == WeaponState.FIRING) {
            // Shot ends
            if (System.currentTimeMillis() >= lastShotTimestamp + shootTimeMs) {
                if (shotsFired >= shootTimes) {
                    weaponState = WeaponState.RELOADING;
                    reloadStartedTimestamp = System.currentTimeMillis();
                } else {
                    weaponState = WeaponState.IDLE;
                }
            }
        } else if (weaponState == WeaponState.RELOADING) {
            // Reload finished
            if (System.currentTimeMillis() >= reloadStartedTimestamp + reloadTimeMs) {
                shotsFired = 0;
                weaponState = WeaponState.IDLE;
            }
        }
    }

    public WeaponState getState() {
        return weaponState;
    }

    protected abstract void initialize();
    public abstract void shoot();


}
