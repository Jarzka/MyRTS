package org.voimala.myrtsengine.screens.gameplay.ammunition;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrtsengine.graphics.SpriteContainer;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractBullet;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public class M4Bullet extends AbstractBullet {

    public M4Bullet(final WorldController worldController1, final double velocity, final long maxDistance) {
        super(worldController1, velocity, maxDistance);
    }

    @Override
    public Sprite getSprite() {
        return SpriteContainer.getInstance().getSprite("m4-bullet");
    }
}
