package org.voimala.myrts.screens.gameplay.multiplayer;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.networking.LocalMultiplayerInfo;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.input.PlayerInput;

import java.util.ArrayList;

public class MultiplayerSynchronizationManager {

    private static final String TAG = MultiplayerSynchronizationManager.class.getName();

    private ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>();
    private GameplayScreen gameplayScreen;
    /** SimTick is used for network communication.
     * 1 simTick = 5 world update ticks by default.
     * When a new SimTick is reached, the game executes other player's input information.
     * If such information is not available, wait for it.
     */
    private long simTick = 0;
    private boolean isWaitingInputForNextSimTick = false;
    private boolean isNoInputSentForTheNextTurn = false;

    public MultiplayerSynchronizationManager(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    /** @return True if input is ok for the next SimTick. */
    public boolean handleNewSimTick() {
        // We never wait input for SimTick 2.
        if (simTick == 1) {
            return true;
        }

        isWaitingInputForNextSimTick = true;
        /* Check that we have input information for the next SimTick so that we can
        * continue executing the simulation. If the input is not available for all players,
        * isWaitingInputForNextSimTick remains true.
        * The input for the next turn was sent in the previous SimTick. */
        if (doesAllInputExist(simTick - 1)) {
            performAllInputs(simTick + 1);
            sendNoInput();
            isWaitingInputForNextSimTick = false;
            simTick++;
            return true;
        }

        return false;
    }
    private void sendNoInput() {
        NetworkManager.getInstance().getClientThread().sendMessage(
                RTSProtocolManager.getInstance().createNetworkMessageInputNoInput(simTick));
        isNoInputSentForTheNextTurn = true;
    }

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

    public void performAllInputs(final long simTick) {
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

    public long getSimTick() {
        return simTick;
    }

    public void setSimTick(final long simTick) {
        this.simTick = simTick;
    }

    public boolean isWaitingInputForNextSimTick() {
        return isWaitingInputForNextSimTick;
    }

}
