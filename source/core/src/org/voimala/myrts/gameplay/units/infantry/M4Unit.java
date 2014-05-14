package org.voimala.myrts.gameplay.units.infantry;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.gameplay.units.movements.CarMovement;
import org.voimala.myrts.graphics.SpriteContainer;

public class M4Unit extends Infantry {

    public M4Unit() {
        initialize();
    }

    protected void initializeDimensions() {
        width = 156;
        height = 252;
    }

    protected void initializeCollisionMask() {
        collisionMask = new Circle(x, y, width / 2);
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
            collisionCircle.setPosition(x, y);
        }

    }
}
