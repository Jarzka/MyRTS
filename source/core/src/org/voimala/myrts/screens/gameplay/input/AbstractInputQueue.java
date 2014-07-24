package org.voimala.myrts.screens.gameplay.input;

import org.voimala.myrts.screens.gameplay.GameplayScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInputQueue {
    /** vector is a thread-safe implementation of ArrayList,
     * but it can not be used here because loopin over the contents
     * might throw ConcurrentModificationException. */
    protected ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>(); // TODO HashMap would be faster?
    protected GameplayScreen gameplayScreen;



    public void setGameplayScreen(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public synchronized List<PlayerInput> findInputsByPlayerNumberAndSimTick(final int playerNumber, final long simTick) {
        List<PlayerInput> playerInputs = new ArrayList<PlayerInput>();

        // TODO Do not use linear search?
        for (PlayerInput playerInput : this.playerInputs) {
            if (playerInput.getPlayerNumber() == playerNumber && playerInput.getSimTick() == simTick) {
                playerInputs.add(playerInput);
            }
        }

        return playerInputs;
    }

    public boolean doesPlayerInputExist(final int playerNumber, final long simTick) {
        if (checkFirstSimTickInput(simTick)) return true;
        return findInputsByPlayerNumberAndSimTick(playerNumber, simTick).size() != 0;
    }

    protected boolean checkFirstSimTickInput(long simTick) {
        // Do not assume any input for this SimTick
        if (simTick == 0) {
            return true;
        }

        return false;
    }

    public void reset() {
        playerInputs.clear();
    }

}
