package org.voimala.myrts.screens.gameplay.units.infantry;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.movements.CarMovement;
import org.voimala.myrts.screens.gameplay.units.turrets.AbstractTurret;
import org.voimala.myrts.screens.gameplay.units.turrets.M4Turret;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public class M4Unit extends AbstractInfantry {

    public M4Unit(WorldController worldController) {
        super(worldController);

        this.maxEnergy = 100;
        this.energy = 100;
    }

    @Override
    protected void initializeTurrets() {
        AbstractTurret turret = new M4Turret(this);
        turret.setAngle(angleDeg);
        turrets.add(turret);
    }

    protected void initializeDimensions() {
        width = 156;
        height = 252;
    }

    protected void initializeCollisionMask() {
        collisionMask = new Circle(position.x, position.y, 70);
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
    public Sprite getSprite() {
        return null; // TODO M4 unit should have a sprite
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
