package org.voimala.myrtsengine.screens.gameplay.weapons;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrtsengine.screens.gameplay.ammunition.M4Bullet;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public class M4 extends AbstractWeapon {

    @Override
    protected void initialize() {
        reloadTimeMs = 3500;
        shootTimeMs = 70;
        shootTimes = 30;
        maxTravelDistance = 2000;
        bulletVelocity = 2000;
    }

    @Override
    /** Shoots if the weapon is not currently being fired or reloading. */
    public AbstractAmmunition shoot(final WorldController worldController, final Vector2 position, final float angle) {
        if (weaponState == WeaponState.IDLE) {
            lastShotTimestamp = System.currentTimeMillis();
            shotsFired++;
            weaponState = WeaponState.FIRING;

            M4Bullet m4Bullet = new M4Bullet(worldController, bulletVelocity, maxTravelDistance);
            m4Bullet.setPosition(position);
            m4Bullet.setAngle(angle);
            return m4Bullet;
        }

        return null;
    }

}
