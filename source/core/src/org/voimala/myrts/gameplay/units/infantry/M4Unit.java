package org.voimala.myrts.gameplay.units.infantry;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrts.graphics.SpriteContainer;

public class M4Unit extends Infantry {

    public M4Unit() {
        initializeMovement();
    }

    @Override
    public Sprite getCurrentSprite() {
        return SpriteContainer.getInstance().getSprite("m4-stopped-0"); // TODO Get current animation frame
    }

    private void initializeMovement() {
        movement.setMaxVelocity(400);
        movement.setAcceleration(500);
        movement.setDeceleration(500);
        movement.setMaxRotationVelocity(600);
        movement.setRotationAcceleration(400);
        movement.setRotationDeceleration(400);
    }
}
