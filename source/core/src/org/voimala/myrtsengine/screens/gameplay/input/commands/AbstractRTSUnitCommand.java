package org.voimala.myrtsengine.screens.gameplay.input.commands;

public abstract class AbstractRTSUnitCommand extends AbstractRTSCommand {

    private long unitId;

    protected AbstractRTSUnitCommand(final long unitId) {
        this.unitId = unitId;
    }

    public long getObjectId() {
        return unitId;
    }
}
