package org.voimala.myrtsengine.screens.gameplay.effects;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class MuzzleFire extends AbstractEffect {

    protected long lifeTimeMs = 50;
    protected long livedLife = 0;

    public MuzzleFire(final WorldController worldController, final Vector2 position, final float angleDeg) {
        super(worldController, position, angleDeg);
    }

}
