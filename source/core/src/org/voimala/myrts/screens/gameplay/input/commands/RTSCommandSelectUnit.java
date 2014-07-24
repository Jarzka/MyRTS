package org.voimala.myrts.screens.gameplay.input.commands;

public class RTSCommandSelectUnit extends AbstractRTSUnitCommand {

    public RTSCommandSelectUnit(final int playerWhoMadeCommand, final long id) {
        super(playerWhoMadeCommand, id);
    }

    @Override
    public RTSCommandType getCommandName() {
        return RTSCommandType.SELECT_UNIT;
    }
}
