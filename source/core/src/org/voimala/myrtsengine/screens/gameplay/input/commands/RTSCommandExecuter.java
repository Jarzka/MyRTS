package org.voimala.myrtsengine.screens.gameplay.input.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.audio.AudioEffect;
import org.voimala.myrtsengine.audio.SoundContainer;
import org.voimala.myrtsengine.networking.ListenSocketThread;
import org.voimala.myrtsengine.networking.NetworkManager;
import org.voimala.myrtsengine.networking.RTSProtocolManager;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.multiplayer.MultiplayerSynchronizationManager;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;

/** This class is used to perform RTS commands. */
public class RTSCommandExecuter {

    private static final String TAG = RTSCommandExecuter.class.getName();

    private GameplayScreen gameplayScreen;

    public RTSCommandExecuter(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public void executeCommand(final ExecuteCommandMethod method, final AbstractRTSCommand command) {
        if (command != null) {
            if (command.getCommandName() == RTSCommand.MOVE_UNIT) {
                RTSCommandMoveUnit moveCommand = (RTSCommandMoveUnit) command;
                handleCommandMoveUnit(method, gameplayScreen.getWorldController().getUnitContainerAllUnits().findUnitById(
                                moveCommand.getObjectId()), moveCommand.getTargetX(), moveCommand.getTargetY()
                );
            } else if (command.getCommandName() == RTSCommand.SELECT_UNIT) {
                RTSCommandSelectUnit selectCommand = (RTSCommandSelectUnit) command;
                handleCommandSelectUnit(method, gameplayScreen.getWorldController().getUnitContainerAllUnits().findUnitById(
                                selectCommand.getObjectId())
                );
            }
        }

    }

    private void handleCommandMoveUnit(final ExecuteCommandMethod method, AbstractUnit unit, final float targetX, final float targetY) {
        if (method == ExecuteCommandMethod.EXECUTE_LOCALLY) {
            unit.getWorldController().getAudioEffectContainer().add(new AudioEffect(
                    unit.getWorldController(),
                    SoundContainer.getInstance().getUnitCommandSound("m4-move"), // TODO Hardcoded value!
                    1f,
                    unit.getX(),
                    unit.getY()));
            unit.getMovement().setSinglePathPoint(new Vector2(targetX, targetY));
        }

        if (method == ExecuteCommandMethod.SEND_TO_NETWORK) {
            String message = RTSProtocolManager.getInstance().createNetworkMessageInputMoveUnit(
                    unit.getObjectId(),
                    MultiplayerSynchronizationManager.getInstance().getSimTick(),
                    new Vector2(targetX, targetY));
            ListenSocketThread listenSocketThread = NetworkManager.getInstance().getClientThread();
            if (listenSocketThread != null) {
                listenSocketThread.sendMessage(message);
            } else {
                Gdx.app.debug(TAG, "WARNING: Unable to send move command to the network because socket is null.");
            }
        }
    }

    private void handleCommandSelectUnit(final ExecuteCommandMethod method, AbstractUnit unit) {
        if (method == ExecuteCommandMethod.EXECUTE_LOCALLY) {
            unit.getWorldController().getAudioEffectContainer().add(new AudioEffect(
                    unit.getWorldController(),
                    SoundContainer.getInstance().getUnitCommandSound("m4-select"), // TODO Hardcoded value!
                    1f,
                    unit.getX(),
                    unit.getY()));
            unit.setSelected(true);
        }

        if (method == ExecuteCommandMethod.SEND_TO_NETWORK) {
            // It is not necessary to send this command to the network.
        }
    }

    /** @return null if message could not be constructed. */
    public static PlayerInput createPlayerInputFromNetworkMessage(final String inputMessage) {
        if (inputMessage.startsWith("<INPUT|UNIT_MOVE|")) {
            return createPlayerInputUnitMove(inputMessage);
        } else if (inputMessage.startsWith("<INPUT|NO_INPUT|")) {
            return createPlayerInputNoInput(inputMessage);
        }

        return null; // TODO Create object
    }

    private static PlayerInput createPlayerInputNoInput(final String inputMessage) {
        String[] messageSplitted = RTSProtocolManager.splitNetworkMessage(inputMessage);

        return new PlayerInput(
                Integer.valueOf(messageSplitted[2]),
                Long.valueOf(messageSplitted[3]),
                null);
    }

    private static PlayerInput createPlayerInputUnitMove(final String inputMessage) {
        String[] messageSplitted = RTSProtocolManager.splitNetworkMessage(inputMessage);

        return new PlayerInput(
                Integer.valueOf(messageSplitted[2]),
                Integer.valueOf(messageSplitted[3]),
                new RTSCommandMoveUnit(Long.valueOf(messageSplitted[4]),
                        Float.valueOf(messageSplitted[5]),
                        Float.valueOf(messageSplitted[6]))
                );
    }
}
