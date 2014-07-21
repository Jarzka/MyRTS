package org.voimala.myrtsengine.screens.gameplay.units.turrets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.voimala.myrtsengine.graphics.SpriteContainer;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.weapons.AbstractWeapon;
import org.voimala.myrtsengine.screens.gameplay.weapons.M4;

public class M4Turret extends AbstractTurret {

    public M4Turret(AbstractUnit owner) {
        super(owner, new M4());

        maxRotationVelocity = 800;
        rotationAcceleration = 800;
        rotationDeceleration = 800;
        range = 2000;
    }

    @Override
    public Sprite getSprite() {
        return SpriteContainer.getInstance().getSprite("m4-stopped-0");
    }

}
