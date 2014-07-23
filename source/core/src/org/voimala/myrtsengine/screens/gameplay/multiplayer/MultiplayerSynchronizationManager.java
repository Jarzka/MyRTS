package org.voimala.myrtsengine.screens.gameplay.multiplayer;

import com.badlogic.gdx.Gdx;
import org.voimala.myrtsengine.app.GameMain;
import org.voimala.myrtsengine.networking.NetworkManager;
import org.voimala.myrtsengine.networking.RTSProtocolManager;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.input.LocalInputQueue;
import org.voimala.myrtsengine.screens.gameplay.input.NetworkInputQueue;

/** This class is used to store ja process player inputs during gameplay.
 * Implemented as a singleton since it is important to be able to store player inputs
 * even if the game is still loading the game. */
public class MultiplayerSynchronizationManager {

    private static final String TAG = MultiplayerSynchronizationManager.class.getName();

    private static MultiplayerSynchronizationManager instanceOfThis;

    private GameplayScreen gameplayScreen;
    /** SimTick is used for network communication.
     * 1 simTick = 5 world update ticks by default.
     * When a new SimTick is reached, the game executes other player's input information.
     * If such information is not available, wait for it.
     */
    private long simTick = 0; /** "Communication turn" in multiplayer game. */
    private boolean isWaitingInputForNextSimTick = false;

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
        if (NetworkInputQueue.getInstance().doesAllInputExistForSimTick(simTick - 1)) {
            LocalInputQueue.getInstance().sendInputsToOtherPlayers(simTick);
            NetworkInputQueue.getInstance().performInputsForSimTick(simTick - 1);
            sendGameStateHash();
            isWaitingInputForNextSimTick = false;
            simTick++;
            Gdx.app.debug(TAG, "Player " + GameMain.getInstance().getPlayer().getNumber() + " world tick is "
                            + gameplayScreen.getWorldUpdateTick() + " and simtick is " + simTick);
            return true;
        }

        return false;
    }

    private void sendGameStateHash() {
        String hash = gameplayScreen.getWorldController().getGameStateHash();

        NetworkManager.getInstance().getClientThread().sendMessage(
                RTSProtocolManager.getInstance().createNetworkMessageGameStateHash(
                        GameMain.getInstance().getPlayer().getNumber(),
                        simTick,
                        hash));
    }

    public long getSimTick() {
        return simTick;
    }

    /** Warning: SimTick should be set manually only when the game is not running! */
    public void setSimTick(final long simTick) {
        this.simTick = simTick;
    }

    public boolean isWaitingInputForNextSimTick() {
        return isWaitingInputForNextSimTick;
    }

}
