package org.voimala.myrts.screens.gameplay.weapons;

import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.units.infantry.AbstractInfantry;

public class WeaponOptions implements Cloneable {

    private int hitPowerAgainstInfantry = 0;
    private int hitPowerAgainstVehicles = 0;
    private int hitPowerAgainstShips = 0;
    private int hitPowerAgainstAircraft = 0;

    private long bulletVelocity = 0;
    private long maxDistance = 0; /// The ammunition will be destroyed if it travels more than this far.

    public WeaponOptions clone() throws CloneNotSupportedException {
        return (WeaponOptions) super.clone();
    }

    public int getHitPowerAgainstInfantry() {
        return hitPowerAgainstInfantry;
    }

    public void setHitPowerAgainstInfantry(final int hitPowerAgainstInfantry) {
        this.hitPowerAgainstInfantry = hitPowerAgainstInfantry;
    }

    public int getHitPowerAgainstVehicles() {
        return hitPowerAgainstVehicles;
    }

    public void setHitPowerAgainstVehicles(final int hitPowerAgainstVehicles) {
        this.hitPowerAgainstVehicles = hitPowerAgainstVehicles;
    }

    public int getHitPowerAgainstShips() {
        return hitPowerAgainstShips;
    }

    public void setHitPowerAgainstShips(final int hitPowerAgainstShips) {
        this.hitPowerAgainstShips = hitPowerAgainstShips;
    }

    public int getHitPowerAgainstAircraft() {
        return hitPowerAgainstAircraft;
    }

    public void setHitPowerAgainstAircraft(final int hitPowerAgainstAircraft) {
        this.hitPowerAgainstAircraft = hitPowerAgainstAircraft;
    }

    public long getBulletVelocity() {
        return bulletVelocity;
    }

    public void setBulletVelocity(long bulletVelocity) {
        this.bulletVelocity = bulletVelocity;
    }

    public long getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(long maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getHitPowerAgainstUnit(final AbstractUnit unit) {
        if (unit instanceof AbstractInfantry) {
            return hitPowerAgainstInfantry;
        }

        return 0;
    }
}
