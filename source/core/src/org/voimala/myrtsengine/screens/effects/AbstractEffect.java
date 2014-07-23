package org.voimala.myrtsengine.screens.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public abstract class AbstractEffect extends AbstractGameObject {

    public AbstractEffect(final WorldController worldController, final Vector2 position, final float angleDeg) {
        super(worldController);

        this.position = new Vector2(position);
        this.angleDeg = angleDeg;
    }

    public AbstractEffect clone() throws CloneNotSupportedException {
        AbstractEffect effectClone = (AbstractEffect) super.clone();
        return effectClone;
    }

}
