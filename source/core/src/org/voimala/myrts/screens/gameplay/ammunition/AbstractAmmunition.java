package org.voimala.myrts.screens.gameplay.ammunition;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrts.screens.gameplay.world.WorldController;

/** Abstract ammunition represents a "physical" weapon in the game world, like bullet, missile etc. */
public abstract class AbstractAmmunition extends AbstractGameObject {

    private static final String TAG = AbstractAmmunition.class.getName();

    public AbstractAmmunition(final WorldController worldController) {
        super(worldController);
    }

    public AbstractAmmunition clone() throws CloneNotSupportedException {
        AbstractGameObject abstractGameObjectClone = super.clone();
        return (AbstractAmmunition) abstractGameObjectClone;
    }

    @Override
    public void initializeId() {
        super.initializeId();
    }
}
