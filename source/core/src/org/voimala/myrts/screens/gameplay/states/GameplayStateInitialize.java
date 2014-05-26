package org.voimala.myrts.screens.gameplay.states;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

public class GameplayStateInitialize extends GameplayState {

    public GameplayStateInitialize(GameplayScreen ownerGameplay) {
        super(ownerGameplay);
    }

    @Override
    public void update() {
        loadMap();
        initializeMultiplayer();
    }

    private void loadMap() {
        // TODO
    }

    private void initializeMultiplayer() {
        // TODO
    }

}
