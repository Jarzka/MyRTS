package org.voimala.myrtsengine.screens.gameplay.ammunition;

import org.voimala.myrtsengine.screens.gameplay.world.AbstractGameObject;

public abstract class AbstractAmmunition extends AbstractGameObject {

    public AbstractAmmunition clone() throws CloneNotSupportedException {
        AbstractGameObject abstractGameObjectClone = super.clone();
        return (AbstractAmmunition) abstractGameObjectClone;
    }
}
