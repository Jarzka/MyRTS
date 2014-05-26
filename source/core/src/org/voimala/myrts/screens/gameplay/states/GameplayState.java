package org.voimala.myrts.screens.gameplay.states;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

public abstract class GameplayState {

    protected GameplayScreen ownerGameplay = null;

    public GameplayState(GameplayScreen ownerGameplay) {
        this.ownerGameplay = ownerGameplay;
    }

    public abstract void update();
}
