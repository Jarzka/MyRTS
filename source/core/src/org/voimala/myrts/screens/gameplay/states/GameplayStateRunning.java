package org.voimala.myrts.screens.gameplay.states;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

public class GameplayStateRunning extends AbstractGameplayState {

    public GameplayStateRunning(GameplayScreen ownerGameplay) {
        super(ownerGameplay);
    }

    @Override
    public void update(float deltaTime) {
        ownerGameplay.handleUserInput(deltaTime);
        ownerGameplay.updateWorld(deltaTime);
        ownerGameplay.renderWorld(deltaTime);
    }

}
