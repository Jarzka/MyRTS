package org.voimala.myrtsengine.screens.gameplay.ammunition;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.movements.BulletMovement;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;

public class Bullet extends AbstractGameObject{

    public Bullet(final long id) {
        super(id);
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
    public boolean onCollision(Vector2 point) {
        if (collisionMask instanceof Circle) {
            Circle collisionCircle = (Circle) collisionMask;
            return collisionCircle.contains(point);
        }

        return false;
    }
}
