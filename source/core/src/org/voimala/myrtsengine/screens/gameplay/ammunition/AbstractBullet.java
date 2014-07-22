package org.voimala.myrtsengine.screens.gameplay.ammunition;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.movements.BulletMovement;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;
import org.voimala.utility.MathHelper;

public abstract class AbstractBullet extends AbstractAmmunition {

    protected Vector2 startPosition = null;
    protected long maxTravelDistance = 0;

    public AbstractBullet(final WorldController worldController1, final double velocity, final long maxTravelDistance) {
        super(worldController1);
        this.movement.setVelocity(velocity);
        this.maxTravelDistance = maxTravelDistance;
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
        checkDistanceLeft();
    }

    private void checkDistanceLeft() {
        if (MathHelper.getDistanceBetweenPoints(position.x, position.y, startPosition.x, startPosition.y) >= maxTravelDistance) {
            die();
        }
    }

    private void die() {
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
