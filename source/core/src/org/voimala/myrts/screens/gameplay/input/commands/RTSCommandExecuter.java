package org.voimala.myrts.screens.gameplay.input.commands;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.networking.ListenSocketThread;
import org.voimala.myrts.networking.NetworkManager;
import org.voimala.myrts.networking.RTSProtocolManager;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.world.GameMode;

/** This class is used to perform RTS commands. */
public class RTSCommandExecuter {

    private GameplayScreen gameplayScreen;

    public RTSCommandExecuter(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public void executeCommand(final AbstractRTSCommand command) {
        if (command.getCommandName() == RTSCommand.MOVE_UNIT) {
            RTSCommandUnitMove moveCommand = (RTSCommandUnitMove) command;
            handleCommandMoveUnit(gameplayScreen.getWorldController().getUnitContainer().findUnitById(
                    moveCommand.getObjectId()), moveCommand.getTargetX(), moveCommand.getTargetY()
            );
        }
    }

    private void handleCommandMoveUnit(AbstractUnit unit, final float targetX, final float targetY) {
        if (gameplayScreen.getWorldController().getGameplayScreen().getGameMode() == GameMode.SINGLEPLAYER) {
            // Process command locally
            unit.getMovement().setPathPoint(new Vector2(targetX, targetY));
        } else if (gameplayScreen.getWorldController().getGameplayScreen().getGameMode() == GameMode.MULTIPLAYER) {
            // Send command to the server
            String message = RTSProtocolManager.getInstance().createNetworkMessageInputMoveUnit(
                    unit.getObjectId(),
                    gameplayScreen.getWorldController().getGameplayScreen().getMultiplayerSynchronizationManager().getSimTick(),
                    new Vector2(targetX, targetY));
            ListenSocketThread listenSocketThread = NetworkManager.getInstance().getClientThread();
            if (listenSocketThread != null) {
                listenSocketThread.sendMessage(message);
            }
        }
    }

    public static PlayerInput createPlayerInputFromNetworkMessage(final String inputMessage) {
        return null; // TODO Create object
    }
}
