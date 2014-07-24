package org.voimala.myrts.screens.gameplay.units.turrets;

public abstract class AbstractTurretState implements Cloneable {

    AbstractTurret ownerTurret;

    public AbstractTurretState clone() throws CloneNotSupportedException {
        return (AbstractTurretState) super.clone();
    }

    public AbstractTurretState(AbstractTurret ownerTurret) {
        this.ownerTurret = ownerTurret;
    }

    public void setOwnerTurret(AbstractTurret ownerTurret) {
        this.ownerTurret = ownerTurret;
    }

    public abstract void updateState(final float deltaTime);
}
