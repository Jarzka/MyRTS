package org.voimala.myrts.screens.gameplay.input.commands;

public abstract class AbstractRTSCommandUnit extends AbstractRTSCommand {

    private long unitId;

    protected AbstractRTSCommandUnit(final long unitId) {
        this.unitId = unitId;
    }

    public long getObjectId() {
        return unitId;
    }
}
