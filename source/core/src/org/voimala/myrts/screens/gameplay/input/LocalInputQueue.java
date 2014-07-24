package org.voimala.myrts.screens.gameplay.input;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandEmpty;

import java.util.ArrayList;
import java.util.List;

/** This class is used to store local player inputs in multiplayer game before they are sent to the network
 * at the end of SimTick. */
public class LocalInputQueue extends AbstractInputQueue {

    private static final String TAG = LocalInputQueue.class.getName();

    private static LocalInputQueue instanceOfThis;

    private LocalInputQueue() {

    }

    public static LocalInputQueue getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new LocalInputQueue();
        }

        return instanceOfThis;
    }

    public void addInput(final PlayerInput playerInput) {
        Gdx.app.log(TAG, "Storing local player input. Player number: " + playerInput.getPlayerNumber() + ". " +
                "SimTick: " + playerInput.getSimTick() + ". " + "Command: " + playerInput.getCommand().getCommandName().toString());
        playerInputs.add(playerInput);
    }

    public void sendInputsToOtherPlayers(final long simTick) {
        Gdx.app.log(TAG, "Preparing to send local player inputs to the network.");
        if (doesPlayerInputExist(GameMain.getInstance().getPlayer().getNumber(), simTick)) {
            List<PlayerInput> inputsToBeSent = findInputsByPlayerNumberAndSimTick(GameMain.getInstance().getPlayer().getNumber(), simTick);
            NetworkManager.getInstance().getClientThread().sendMessage(
                    RTSProtocolManager.getInstance().createNetworkMessageFromPlayerInputs(inputsToBeSent, simTick));
        } else {
            ArrayList<PlayerInput> playerInputsEmpty = new ArrayList<PlayerInput>();
            playerInputsEmpty.add(new PlayerInput(GameMain.getInstance().getPlayer().getNumber(), simTick,
                    new RTSCommandEmpty(GameMain.getInstance().getPlayer().getNumber())));
            NetworkManager.getInstance().getClientThread().sendMessage(
                        RTSProtocolManager.getInstance().createNetworkMessageFromPlayerInputs(playerInputsEmpty, simTick));
        }

        removeOldInputs();
    }

    private void removeOldInputs() {
        // TODO Remove old inputs
    }
}
