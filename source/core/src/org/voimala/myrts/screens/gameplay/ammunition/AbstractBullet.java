package org.voimala.myrts.screens.gameplay.ammunition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.movements.BulletMovement;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.weapons.WeaponOptions;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.utility.MathHelper;

public abstract class AbstractBullet extends AbstractAmmunition {

    private static final String TAG = AbstractBullet.class.getName();

    protected Vector2 startPosition = null;
    protected WeaponOptions weaponOptions;

    public AbstractBullet(final WorldController worldController1, WeaponOptions weaponOptions) {
        super(worldController1);
        this.weaponOptions = weaponOptions;
        this.movement.setVelocity(weaponOptions.getBulletVelocity());
    }

    public AbstractBullet clone() throws CloneNotSupportedException {
        AbstractBullet bulletClone = (AbstractBullet) super.clone();

        bulletClone.setStartPosition(new Vector2(startPosition.x, startPosition.y));

        return bulletClone;
    }

    @Override
    protected void initializeDimensions() {
        width = 2;
        height = 2;
    }

    protected void initializeCollisionMask() {
        collisionMask = new Circle(position.x, position.y, 2);
    }

    @Override
    protected void initializeMovement() {
        movement = new BulletMovement(this);
    }

    @Override
    protected void updateCollisionMask() {
        if (collisionMask instanceof Circle) {
            Circle collisionCircle = (Circle) collisionMask;
            collisionCircle.setPosition(position.x, position.y);
        }
    }

    @Override
    public void updateState(final float deltaTime) {
        super.updateState(deltaTime);
        checkCollision();
        checkDistanceLeft();
    }

    private void checkCollision() {
        for (AbstractUnit unit : worldController.getUnitContainer().getAllUnits()) {
            if (unit.onCollision(position)) {
                unit.decreaseEnergy(weaponOptions.getHitPowerAgainstUnit(unit));
                die();
            }
        }

        // TODO Check also other obstacles like buildings, rocks, trees etc.
    }

    private void checkDistanceLeft() {
        if (MathHelper.getDistanceBetweenPoints(position.x, position.y, startPosition.x, startPosition.y)
                >= weaponOptions.getMaxDistance()) {
            die();
        }
    }

    private void die() {
        Gdx.app.debug(TAG, "Bullet " + getObjectId() + " died.");
        worldController.tagAmmunitionToBeRemovedInNextWorldUpdate(this);
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position = position;

        if (startPosition == null) {
            startPosition = new Vector2(position.x, position.y);
        }
    }

    @Override
    public boolean onCollision(Vector2 point) {
        if (collisionMask instanceof Circle) {
            Circle collisionCircle = (Circle) collisionMask;
            return collisionCircle.contains(point);
        }

        return false;
    }

    /** This method should be called only in the cloning process. */
    public void setStartPosition(final Vector2 startPosition) {
        this.startPosition = startPosition;
    }
}
