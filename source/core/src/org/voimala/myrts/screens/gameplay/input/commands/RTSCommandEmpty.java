package org.voimala.myrts.screens.gameplay.input.commands;

public class RTSCommandEmpty extends AbstractRTSCommand {

    public RTSCommandEmpty(final int playerWhoMadeCommand) {
        super(playerWhoMadeCommand);
    }

    @Override
    public RTSCommandType getCommandName() {
        return RTSCommandType.EMPTY;
    }
}
