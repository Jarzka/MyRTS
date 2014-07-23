package org.voimala.myrtsengine.screens.gameplay.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractEffect extends AbstractGameObject {

    private static final String TAG = AbstractEffect.class.getName();

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

    @Override
    public void initializeId() {
        super.initializeId();
        Gdx.app.debug(TAG, "New effect created with id " + getObjectId());
        Gdx.app.debug(TAG, "World is predicted: " + worldController.isPredictedWorld());
    }

    public float getLivedLifeAsPercent() {
        return (float) livedLife / (float) lifeTimeMs;
    }

}
