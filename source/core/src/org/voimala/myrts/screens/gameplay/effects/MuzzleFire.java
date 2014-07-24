package org.voimala.myrts.screens.gameplay.effects;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public abstract class MuzzleFire extends AbstractEffect {

    public MuzzleFire(final WorldController worldController, final Vector2 position, final float angleDeg) {
        super(worldController, position, angleDeg);

        lifeTimeMs = 50;
        livedLife = 0;
    }

}
