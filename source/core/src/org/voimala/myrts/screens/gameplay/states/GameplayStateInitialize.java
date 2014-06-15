package org.voimala.myrts.screens.gameplay.states;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

public class GameplayStateInitialize extends AbstractGameplayState {

    public GameplayStateInitialize(GameplayScreen ownerGameplay) {
        super(ownerGameplay);
    }

    @Override
    public void update() {
        loadMap();
    }

    private void loadMap() {
        // TODO Implement file maps
    }

}
