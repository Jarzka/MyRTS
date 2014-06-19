package org.voimala.myrts.screens.gameplay.input.commands;

public class RTSCommandUnitMove extends AbstractRTSCommandUnit {

    private float targetX;
    private float targetY;

    public RTSCommandUnitMove(final long id, final float targetX, final float targetY) {
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
