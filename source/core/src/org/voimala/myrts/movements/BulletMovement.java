package org.voimala.myrts.movements;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.world.AbstractGameObject;

public class BulletMovement extends AbstractMovement {

    public BulletMovement(final AbstractGameObject owner) {
        super(owner);
    }

    @Override
    public void update(float deltaTime) {
        handlePhysicalMotion(deltaTime);
    }

    private void handlePhysicalMotion(float deltaTime) {
        handleVelocity(deltaTime);
    }

    private void handleVelocity(float deltaTime) {
        Vector2 nextPosition = new Vector2(
                (float) (owner.getX() + Math.cos(owner.getAngleInRadians()) * currentVelocity * deltaTime),
                (float) (owner.getY() + Math.sin(owner.getAngleInRadians()) * currentVelocity * deltaTime));
        owner.setPosition(nextPosition);
    }

}
