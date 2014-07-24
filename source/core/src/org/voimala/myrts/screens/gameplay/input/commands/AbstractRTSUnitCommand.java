package org.voimala.myrts.screens.gameplay.input.commands;

public abstract class AbstractRTSUnitCommand extends AbstractRTSCommand {

    private long unitId;

    protected AbstractRTSUnitCommand(final int playerWhoMadeCommand, final long unitId) {
        super(playerWhoMadeCommand);
        this.unitId = unitId;
    }

    public long getObjectId() {
        return unitId;
    }
}
