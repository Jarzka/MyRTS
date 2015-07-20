package org.voimala.myrts.screens.gameplay.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrts.screens.gameplay.ammunition.M4Bullet;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public class M4 extends AbstractWeapon {

    private static final String TAG = AbstractWeapon.class.getName();

    @Override
    protected void initialize() {
        reloadTimeSeconds = 2;
        shootTimeSeconds = 0.12f;
        shootTimes = 30;

        weaponOptions.setMaxDistance(2000);
        weaponOptions.setBulletVelocity(2000);
        weaponOptions.setHitPowerAgainstInfantry(15);
    }

    @Override
    /** Shoots if the weapon is not currently being fired or reloading. */
    public AbstractAmmunition tryToShoot(
            final WorldController worldController,
            final Vector2 position,
            final float angle,
            final WeaponOptions weaponOptions) {
        if (weaponState == WeaponState.IDLE) {
            weaponState = WeaponState.FIRING;
            shotsFired++;

            M4Bullet m4Bullet = null;
            try {
                m4Bullet = new M4Bullet(
                        worldController,
                        weaponOptions.clone());
            } catch (CloneNotSupportedException e) {
                Gdx.app.debug(TAG, "WARNING: CloneNotSupportedException when cloning Weapon: " + e.getMessage());
            }
            m4Bullet.setPosition(position);
            m4Bullet.setAngle(angle);
            return m4Bullet;
        }

        return null;
    }

}
