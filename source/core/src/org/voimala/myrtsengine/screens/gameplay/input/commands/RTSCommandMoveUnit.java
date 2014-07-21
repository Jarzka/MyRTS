package org.voimala.myrtsengine.screens.gameplay.input.commands;

public class RTSCommandMoveUnit extends AbstractRTSUnitCommand {

    private float targetX;
    private float targetY;

    public RTSCommandMoveUnit(final long id, final float targetX, final float targetY) {
        super(id);

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
    public RTSCommand getCommandName() {
        return RTSCommand.MOVE_UNIT;
    }
}
