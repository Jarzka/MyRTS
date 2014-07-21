package org.voimala.myrtsgame.screens.gameplay.units.infantry;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.movements.CarMovement;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.units.infantry.AbstractInfantry;
import org.voimala.myrtsengine.screens.gameplay.units.turrets.Turret;
import org.voimala.myrtsengine.screens.gameplay.weapons.M4;

public class M4Unit extends AbstractInfantry {

    public M4Unit(final long id) {
        super(id);
    }

    @Override
    protected void initializeTurrets() {
        turrets.add(new Turret(AbstractUnit.getNextFreeId(), this, new M4()));
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
