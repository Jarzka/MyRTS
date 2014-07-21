package org.voimala.myrts.screens.gameplay.units.turrets;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrts.screens.gameplay.world.AbstractGameObject;

public class Turret extends AbstractGameObject {

    private AbstractUnit owner;
    private AbstractUnit target;
    private AbstractWeapon weapon;

    public Turret(final long id, AbstractUnit owner, AbstractWeapon weapon) {
        super(id);
        this.owner = owner;
        this.weapon = weapon;
    }

    public void updateState(final float deltaTime) {
        updateWeaponState();
    }

    private void updateWeaponState() {

    }

    @Override
    protected void initializeDimensions() {
        width = 156;
        height = 252;
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
}
