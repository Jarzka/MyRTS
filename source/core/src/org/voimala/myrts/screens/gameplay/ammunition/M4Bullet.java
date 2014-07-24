package org.voimala.myrts.screens.gameplay.ammunition;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.screens.gameplay.weapons.WeaponOptions;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public class M4Bullet extends AbstractBullet {

    public M4Bullet(final WorldController worldController1, final WeaponOptions weaponOptions) {
        super(worldController1, weaponOptions);
    }

    @Override
    public Sprite getSprite() {
        return SpriteContainer.getInstance().getSprite("m4-bullet");
    }
}
