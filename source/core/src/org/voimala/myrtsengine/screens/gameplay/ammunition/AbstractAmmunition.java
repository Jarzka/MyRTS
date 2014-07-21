package org.voimala.myrtsengine.screens.gameplay.ammunition;

import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

/** Abstract ammunition represents a "physical" weapon in the game world, like bullet, missile etc. */
public abstract class AbstractAmmunition extends AbstractGameObject {

    public AbstractAmmunition(final WorldController worldController) {
        super(worldController);
    }

    public AbstractAmmunition clone() throws CloneNotSupportedException {
        AbstractGameObject abstractGameObjectClone = super.clone();
        return (AbstractAmmunition) abstractGameObjectClone;
    }
}
