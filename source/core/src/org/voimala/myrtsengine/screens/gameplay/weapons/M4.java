package org.voimala.myrtsengine.screens.gameplay.weapons;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrtsengine.screens.gameplay.ammunition.M4Bullet;

public class M4 extends AbstractWeapon {

    @Override
    protected void initialize() {
        reloadTimeMs = 2000;
        shootTimeMs = 100;
        shootTimes = 30;
    }

    @Override
    /** Shoots if the weapon is not currently being fired or reloading. */
    public AbstractAmmunition shoot(final Vector2 position, final float angle) {
        if (weaponState == WeaponState.IDLE) {
            lastShotTimestamp = System.currentTimeMillis();
            shotsFired++;
            weaponState = WeaponState.FIRING;

            M4Bullet m4Bullet = new M4Bullet();
            m4Bullet.setPosition(position);
            m4Bullet.setAngle(angle);
            return m4Bullet;
        }

        return null;
    }

}
