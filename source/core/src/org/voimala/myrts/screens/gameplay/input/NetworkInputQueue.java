package org.voimala.myrts.screens.gameplay.input;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.networking.LocalMultiplayerInfo;
import org.voimala.myrts.screens.gameplay.input.commands.ExecuteCommandMethod;

import java.util.List;

/** This class is used to store player inputs from the network before they are executed at the beginning of
 * the correct SimTick. */
public class NetworkInputQueue extends AbstractInputQueue {

    private static final String TAG = NetworkInputQueue.class.getName();

    private static NetworkInputQueue instanceOfThis;

    private NetworkInputQueue() {

    }

    public static NetworkInputQueue getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new NetworkInputQueue();
        }

        return instanceOfThis;
    }

    /** @param simTick SimTick that was active when input was given. */
    public synchronized boolean doesAllInputExistForSimTick(final long simTick) {
        if (checkFirstSimTickInput(simTick)) return true;

        for (int i = 1; i <= 8; i++) {
            if (!LocalMultiplayerInfo.getInstance().getSlots().get(i).startsWith("PLAYER")) {
                continue; // No-one plays in this slot so we do not wait input from this slot.
            }

            if (!doesPlayerInputExist(i, simTick)) {
                Gdx.app.debug(TAG, "Player " + i + " input for simtick " + simTick + " is missing (up to now).");
                return false;
            }
        }

        return true;
    }

    public synchronized void addPlayerInputToQueue(final PlayerInput playerInput) {
        Gdx.app.log(TAG, "Storing network player input. Player number: " + playerInput.getPlayerNumber() + ". " +
        "SimTick: " + playerInput.getSimTick() + ". " + "Command: " + playerInput.getCommand().getCommandName().toString());
        playerInputs.add(playerInput);
    }

    public synchronized void performInputsForSimTick(final long simTick) {
        for (int i = 1; i <= 8; i++) {
            if (!LocalMultiplayerInfo.getInstance().getSlots().get(i).startsWith("PLAYER")) {
                continue; // No-one plays in this slot so we do not wait input from this slot.
            }

            List<PlayerInput> playerInputs = findInputsByPlayerNumberAndSimTick(i, simTick);

            for (PlayerInput playerInput : playerInputs) {
                gameplayScreen.getRTSCommandExecuter().executeCommand(
                        ExecuteCommandMethod.EXECUTE_LOCALLY,
                        playerInput.getCommand());
            }
        }
    }

}
