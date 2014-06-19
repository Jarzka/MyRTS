package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.screens.gameplay.input.PlayerInput;

import java.util.ArrayList;

/** This class used for multiplayer game synchronization. */
public class MultiplayerInputManager {

    private ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>();

    public void addPlayerInputToQueue(final String inputMessage) {
        String[] inputMessageSplitted = RTSProtocolManager.splitNetworkMessage(inputMessage);
        playerInputs.add(new PlayerInput(
                        Integer.valueOf(inputMessageSplitted[3]),
                        Integer.valueOf(inputMessageSplitted[2]),
                        inputMessage)
        );
    }

    /** @return Empty string if input is not found. */
    public String findPlayerInput(final int playerNumber, final long simTick) {
        // TODO Do not use linear search?
        for (PlayerInput playerInput : playerInputs) {
            if (playerInput.getPlayerNumber() == playerNumber && playerInput.getSimTick() == simTick) {
                Gdx.app.debug(TAG, "Player" + " " + playerNumber + " " + "input found for SimTick" + " " + simTick + ": "
                        + playerInput.getInput());
                return playerInput.getInput();
            }
        }

        return "";
    }

    public boolean doesPlayerInputExist(final int playerNumber, final long simTick) {
        return findPlayerInput(playerNumber, simTick).length() != 0;
    }

    public void performNetworkInput() {
        /* TODO
        AbstractUnit unit = worldController.findUnitById(messageSplitted[3]);
        if (unit != null) {
            unit.getMovement().setPathPoint(
                    new Vector2(Float.valueOf(messageSplitted[4]),
                            Float.valueOf(messageSplitted[5])));
        }
        */
    }

    public boolean doesAllInputExist(final long simTick) {
        for (int i = 1; i <= 8; i++) {
            if (!LocalMultiplayerInfo.getInstance().getSlots().get(i).startsWith("PLAYER")) {
                continue; // No-one plays in this slot so we do not wait input from this slot.
            }

            if (!doesPlayerInputExist(i, simTick)) {
                return false;
            }
        }

        return true;
    }

}
