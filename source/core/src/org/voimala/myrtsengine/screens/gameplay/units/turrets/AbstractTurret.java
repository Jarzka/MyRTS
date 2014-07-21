package org.voimala.myrtsengine.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.utility.MathHelper;

public abstract class AbstractTurret extends AbstractGameObject {

    private AbstractUnit owner;
    private AbstractUnit target;
    private AbstractWeapon weapon;
    private Vector2 relativePosition = new Vector2(0, 0); // Turrets position relative to the owner unit.

    private long range = 100;

    public AbstractTurret(AbstractUnit owner, AbstractWeapon weapon) {
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
        updatePosition();
        checkTarget();
    }

    protected void updatePosition() {
        position.x = owner.getX() + relativePosition.x;
        position.y = owner.getY() + relativePosition.y;
    }

    private void checkTarget() {
        if (hasTarget() && isTargetInRange()) {
            AbstractAmmunition ammunition = weapon.shoot(getPosition(), angle);
            if (ammunition != null) {
                owner.getWorldController().getAmmunitionContainer().add(ammunition);
            }
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

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(Vector2 relativePosition) {
        this.relativePosition = relativePosition;
    }
}
