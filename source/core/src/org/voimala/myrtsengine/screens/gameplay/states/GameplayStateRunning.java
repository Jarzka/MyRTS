package org.voimala.myrtsengine.screens.gameplay.states;

import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;

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
