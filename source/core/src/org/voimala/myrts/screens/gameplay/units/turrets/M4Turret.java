package org.voimala.myrts.screens.gameplay.units.turrets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.graphics.SpriteContainer;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.weapons.M4;

public class M4Turret extends AbstractTurret {

    public M4Turret(AbstractUnit owner) {
        super(owner, new M4());

        relativePosition = new Vector2(0, 0);
        relativeShootPosition = new Vector2(180, -13);

        maxRotationVelocity = 800;
        rotationAcceleration = 800;
        rotationDeceleration = 800;
        range = 2000;
        accuracy = 10;
    }

    @Override
    public Sprite getSprite() {
        // TODO "enemytemp" sprite is temporary solution until colors can be set in runtime.
        if (ownerUnit.getPlayerNumber() == 1) {
            return SpriteContainer.getInstance().getSprite("m4-stopped-0");
        } else {
            return SpriteContainer.getInstance().getSprite("m4-stopped-0_enemytemp");
        }
    }

}
