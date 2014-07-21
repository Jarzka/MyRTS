package org.voimala.myrtsengine.screens.gameplay.ammunition;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrtsengine.graphics.SpriteContainer;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractBullet;

public class M4Bullet extends AbstractBullet {

    public M4Bullet() {
        movement.setVelocity(1000);
    }

    @Override
    public Sprite getSprite() {
        return SpriteContainer.getInstance().getSprite("m4-bullet");
    }
}
