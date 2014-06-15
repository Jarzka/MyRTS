package org.voimala.myrts.screens.gameplay.states;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

public abstract class AbstractGameplayState {

    protected GameplayScreen ownerGameplay = null;

    public AbstractGameplayState(GameplayScreen ownerGameplay) {
        this.ownerGameplay = ownerGameplay;
    }

    public abstract void update(final float deltaTime);
}
