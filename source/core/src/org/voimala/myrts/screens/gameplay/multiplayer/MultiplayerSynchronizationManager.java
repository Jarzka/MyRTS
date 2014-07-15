package org.voimala.myrts.screens.gameplay.multiplayer;

import com.badlogic.gdx.Gdx;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.networking.LocalMultiplayerInfo;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.input.commands.ExecuteCommandMethod;
import org.voimala.myrts.screens.gameplay.input.commands.PlayerInput;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommand;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandExecuter;
import org.voimala.myrts.screens.gameplay.units.UnitContainer;

import java.util.ArrayList;
import java.util.List;

/** This class is used to store ja process player inputs during gameplay.
 * Implemented as a singleton since it is important to be able to store player inputs
 * even if the game is still loading the game. */
public class MultiplayerSynchronizationManager {

    private static final String TAG = MultiplayerSynchronizationManager.class.getName();

    private static MultiplayerSynchronizationManager instanceOfThis;

    /** vector is a thread-safe implementation of ArrayList,
     * but it can not be used here because loopin over the contents
     * might throw ConcurrentModificationException. */
    private ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>(); // TODO Use different data structure?
    private GameplayScreen gameplayScreen;
    /** SimTick is used for network communication.
     * 1 simTick = 5 world update ticks by default.
     * When a new SimTick is reached, the game executes other player's input information.
     * If such information is not available, wait for it.
     */
    private long simTick = 0;
    private boolean isWaitingInputForNextSimTick = false;
    private boolean isNoInputSentForTheNextTurn = false;

    private MultiplayerSynchronizationManager() {}

    public static MultiplayerSynchronizationManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new MultiplayerSynchronizationManager();
        }

        return instanceOfThis;
    }

    public void setGameplayScreen(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    /** Gameplayscreen should not be null when this method is called.
     * @return True if input is ok for the next SimTick. */
    public boolean handleNewSimTick() {
        if (gameplayScreen == null) {
            throw new NullPointerException("Gameplay screen is null!");
        }

        isWaitingInputForNextSimTick = true;
        /* Check that we have input information for the next SimTick so that we can
        * continue executing the simulation. If the input is not available for all players,
        * isWaitingInputForNextSimTick remains true.
        * The input for the next turn was sent in the previous SimTick. */
        if (doesAllInputExistForSimTick(simTick - 1)) {
            performAllInputs(simTick - 1);
            sendNoInput();
            sendGameStateHash();
            isWaitingInputForNextSimTick = false;
            simTick++;
            Gdx.app.debug(TAG, "Player " + GameMain.getInstance().getPlayer().getNumber() + " world tick is "
                            + gameplayScreen.getWorldUpdateTick() + " and simtick is " + simTick);
            removeOldInputs();
            return true;
        }

        return false;
    }

    private void sendNoInput() {
        NetworkManager.getInstance().getClientThread().sendMessage(
                RTSProtocolManager.getInstance().createNetworkMessageInputNoInput(simTick));
        isNoInputSentForTheNextTurn = true;
    }

    private void sendGameStateHash() {
        String hash = gameplayScreen.getGameStateHash();

        NetworkManager.getInstance().getClientThread().sendMessage(
                RTSProtocolManager.getInstance().createNetworkMessageGameStateHash(
                        GameMain.getInstance().getPlayer().getNumber(),
                        simTick,
                        hash));
    }

    private void removeOldInputs() {
        // TODO Remove old inputs
    }

    public synchronized void addPlayerInputToQueue(final String inputMessage) {
        playerInputs.add(RTSCommandExecuter.createPlayerInputFromNetworkMessage(inputMessage));
    }

    public synchronized void performAllInputs(final long simTick) {
        for (int i = 1; i <= 8; i++) {
            if (!LocalMultiplayerInfo.getInstance().getSlots().get(i).startsWith("PLAYER")) { // TODO Use object here
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

    public synchronized List<PlayerInput> findInputsByPlayerNumberAndSimTick(final int playerNumber, final long simTick) {
        List<PlayerInput> playerInputs = new ArrayList<PlayerInput>();

        // TODO Do not use linear search
        for (PlayerInput playerInput : this.playerInputs) {
            if (playerInput.getPlayerNumber() == playerNumber && playerInput.getSimTick() == simTick) {
                playerInputs.add(playerInput);
            }
        }

        return playerInputs;
    }

    /** @param simTick SimTick that was active when input was given. */
    public synchronized boolean doesAllInputExistForSimTick(final long simTick) {
        if (checkFirstSimTickInput(simTick)) return true;

        for (int i = 1; i <= 8; i++) {
            if (!LocalMultiplayerInfo.getInstance().getSlots().get(i).startsWith("PLAYER")) { // TODO Use object here
                continue; // No-one plays in this slot so we do not wait input from this slot.
            }

            if (!doesPlayerInputExist(i, simTick)) {
                Gdx.app.debug(TAG, "Player " + i + " input for simtick " + simTick + " is still missing. Waiting...");
                return false;
            }
        }

        return true;
    }

    public boolean doesPlayerInputExist(final int playerNumber, final long simTick) {
        if (checkFirstSimTickInput(simTick)) return true;
        return findInputsByPlayerNumberAndSimTick(playerNumber, simTick).size() != 0;
    }

    private boolean checkFirstSimTickInput(long simTick) {
        if (simTick == 0) {
            return true;
        }

        return false;
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
