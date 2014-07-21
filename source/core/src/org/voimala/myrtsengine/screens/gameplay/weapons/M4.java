package org.voimala.myrtsengine.screens.gameplay.weapons;

public class M4 extends AbstractWeapon {

    @Override
    protected void initialize() {
        reloadTimeMs = 2000;
        shootTimeMs = 100;
        shootTimes = 30;
    }

    @Override
    /** Shoots if the weapon is not currently being fired or reloading. */
    public void shoot() {
        if (weaponState == WeaponState.IDLE) {
            lastShotTimestamp = System.currentTimeMillis();
            shotsFired++;
            weaponState = WeaponState.FIRING;

            // TODO Fire
        }
    }

}
