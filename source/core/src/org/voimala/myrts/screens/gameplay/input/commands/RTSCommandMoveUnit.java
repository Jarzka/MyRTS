package org.voimala.myrts.screens.gameplay.input.commands;

import com.badlogic.gdx.math.Vector2;

public class RTSCommandMoveUnit extends AbstractRTSUnitCommand {

    private Vector2 targetPosition;

    public RTSCommandMoveUnit(final int playerWhoMadeCommand,
                              final long unitId,
                              final Vector2 position) {
        super(playerWhoMadeCommand, unitId);
        this.targetPosition = new Vector2(position.x, position.y);
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }

    @Override
    public RTSCommandType getCommandName() {
        return RTSCommandType.MOVE_UNIT;
    }
}
