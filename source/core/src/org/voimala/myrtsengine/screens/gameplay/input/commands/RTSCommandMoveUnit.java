package org.voimala.myrtsengine.screens.gameplay.input.commands;

public class RTSCommandMoveUnit extends AbstractRTSUnitCommand {

    private float targetX;
    private float targetY;

    public RTSCommandMoveUnit(final int playerWhoMadeCommand,
                              final long unitId,
                              final float targetX,
                              final float targetY) {
        super(playerWhoMadeCommand, unitId);

        this.targetX = targetX;
        this.targetY = targetY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    @Override
    public RTSCommandType getCommandName() {
        return RTSCommandType.MOVE_UNIT;
    }
}
