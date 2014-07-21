package org.voimala.myrts.screens.gameplay.units.infantry;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.movements.CarMovement;

public class M4Unit extends AbstractInfantry {

    public M4Unit(final long id) {
        super(id);
    }

    protected void initializeDimensions() {
        width = 50;
        height = 50;
    }

    protected void initializeCollisionMask() {
        collisionMask = new Circle(position.x, position.y, width / 2);
    }

    protected void initializeMovement() {
        movement = new CarMovement(this);
        movement.setMaxVelocity(400);
        movement.setAcceleration(500);
        movement.setDeceleration(500);
        movement.setMaxRotationVelocity(600);
        movement.setRotationAcceleration(400);
        movement.setRotationDeceleration(400);
    }

    @Override
    public boolean onCollision(Vector2 point) {
        if (collisionMask instanceof Circle) {
            Circle collisionCircle = (Circle) collisionMask;
            return collisionCircle.contains(point);
        }

        return false;
    }

    @Override
    protected void updateCollisionMask() {
        if (collisionMask instanceof Circle) {
            Circle collisionCircle = (Circle) collisionMask;
            collisionCircle.setPosition(position.x, position.y);
        }

    }
}
