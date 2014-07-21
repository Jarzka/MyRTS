package org.voimala.myrtsengine.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.utility.MathHelper;

public class Turret extends AbstractGameObject {

    private AbstractUnit owner;
    private AbstractUnit target;
    private AbstractWeapon weapon;

    private long range = 100;

    public Turret(AbstractUnit owner, AbstractWeapon weapon) {
        this.owner = owner;
        this.weapon = weapon;
    }

    public void updateState(final float deltaTime) {
        updateWeaponState();
        updateTurretState();
    }

    private void updateWeaponState() {
        if (weapon != null) {
            weapon.updateState();
        }
    }


    private void updateTurretState() {
        if (hasTarget() && isTargetInRange()) {
            weapon.shoot(getPosition(), angle);
        }
    }

    private boolean hasTarget() {
        return target != null;
    }

    @Override
    protected void initializeDimensions() {
        // TODO Can be left null.
    }

    @Override
    protected void initializeCollisionMask() {
        // TODO Can be left null.
    }

    @Override
    protected void initializeMovement() {
        // TODO Can be left null.
    }

    @Override
    protected void updateCollisionMask() {
        // TODO Can be left null.
    }

    @Override
    public boolean onCollision(Vector2 point) {
        return false;
    }

    public boolean isTargetInRange() {
        if (hasTarget()) {
            return MathHelper.getDistanceBetweenPoints(position.x, position.y, target.getX(), target.getY()) <= range;
        }

        return false;
    }
}
