package org.voimala.myrtsengine.screens.gameplay.effects;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractEffect extends AbstractGameObject {

    protected long lifeTimeMs = 50;
    protected long livedLife = 0;

    public AbstractEffect(final WorldController worldController, final Vector2 position, final float angleDeg) {
        super(worldController);

        this.position = new Vector2(position);
        this.angleDeg = angleDeg;
    }

    public AbstractEffect clone() throws CloneNotSupportedException {
        AbstractEffect effectClone = (AbstractEffect) super.clone();
        return effectClone;
    }

    public float getLivedLifeAsPercent() {
        return (float) livedLife / (float) lifeTimeMs;
    }

}
