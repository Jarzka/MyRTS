package org.voimala.myrtsengine.screens.gameplay.input.commands;

public class RTSCommandSelectUnit extends AbstractRTSUnitCommand {

    public RTSCommandSelectUnit(final long id) {
        super(id);
    }

    @Override
    public RTSCommand getCommandName() {
        return RTSCommand.SELECT_UNIT;
    }
}
