package org.voimala.myrtsengine.screens.gameplay.input;

import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.app.GameMain;
import org.voimala.myrtsengine.audio.AudioEffect;
import org.voimala.myrtsengine.audio.SoundContainer;
import org.voimala.myrtsengine.screens.gameplay.input.commands.*;
import org.voimala.myrtsengine.screens.gameplay.multiplayer.MultiplayerSynchronizationManager;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.world.GameMode;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

/** This class is used to perform RTS commands. */
public class RTSCommandExecuter {

    private static final String TAG = RTSCommandExecuter.class.getName();

    private WorldController worldController;

    public RTSCommandExecuter(final WorldController worldController) {
        this.worldController = worldController;
    }

    public void executeCommand(final ExecuteCommandMethod method, final AbstractRTSCommand command) {
        if (command != null) {
            if (command.getCommandName() == RTSCommandType.MOVE_UNIT) {
                RTSCommandMoveUnit moveCommand = (RTSCommandMoveUnit) command;
                handleCommandMoveUnit(method, moveCommand);
            } else if (command.getCommandName() == RTSCommandType.SELECT_UNIT) {
                RTSCommandSelectUnit selectCommand = (RTSCommandSelectUnit) command;
                handleCommandSelectUnit(method, selectCommand);
            }
        }

    }

    private void handleCommandMoveUnit(final ExecuteCommandMethod method, final RTSCommandMoveUnit rtsCommandMoveUnit) {
        AbstractUnit unit = worldController.getUnitContainerAllUnits().findUnitById(rtsCommandMoveUnit.getObjectId());

        if (method == ExecuteCommandMethod.EXECUTE_LOCALLY) {
            unit.getMovement().setSinglePathPoint(new Vector2(rtsCommandMoveUnit.getTargetPosition().x, rtsCommandMoveUnit.getTargetPosition().y));

            // Play sound effect if local player made the command in singleplayer game
            if (worldController.getGameplayScreen().getGameMode() == GameMode.SINGLEPLAYER
                    && rtsCommandMoveUnit.getPlayerWhoMadeCommand() == GameMain.getInstance().getPlayer().getNumber()) {
                worldController.getAudioEffectContainer().add(new AudioEffect(
                        unit.getWorldController(),
                        SoundContainer.getInstance().getUnitCommandSound("m4-move"), // TODO Hardcoded value!
                        1f,
                        new Vector2(unit.getX(), unit.getY())));
            }
        }

        if (method == ExecuteCommandMethod.ADD_TO_LOCAL_INPUT_QUEUE) {
            LocalInputQueue.getInstance().addInput(new PlayerInput(
                    GameMain.getInstance().getPlayer().getNumber(),
                    MultiplayerSynchronizationManager.getInstance().getSimTick(),
                    rtsCommandMoveUnit));

            // Play sound effect if local player made the command
            if (rtsCommandMoveUnit.getPlayerWhoMadeCommand() == GameMain.getInstance().getPlayer().getNumber()) {
                worldController.getAudioEffectContainer().add(new AudioEffect(
                        unit.getWorldController(),
                        SoundContainer.getInstance().getUnitCommandSound("m4-move"), // TODO Hardcoded value!
                        1f,
                        new Vector2(unit.getX(), unit.getY())));
            }
        }

    }

    private void handleCommandSelectUnit(final ExecuteCommandMethod method, final RTSCommandSelectUnit rtsCommandSelectUnit) {
        AbstractUnit unit = worldController.getUnitContainerAllUnits().findUnitById(rtsCommandSelectUnit.getObjectId());

        if (method == ExecuteCommandMethod.EXECUTE_LOCALLY) {
            unit.setSelected(true);

            // Play sound effect if local player made the command
            if (rtsCommandSelectUnit.getPlayerWhoMadeCommand() == GameMain.getInstance().getPlayer().getNumber()) {
                unit.getWorldController().getAudioEffectContainer().add(new AudioEffect(
                        unit.getWorldController(),
                        SoundContainer.getInstance().getUnitCommandSound("m4-select"), // TODO Hardcoded value!
                        1f,
                        new Vector2(unit.getX(), unit.getY())));
                unit.setSelected(true);
            }
        }

        // Note: it is not necessary to sendInputsToOtherPlayers this command to the network.
    }

}
