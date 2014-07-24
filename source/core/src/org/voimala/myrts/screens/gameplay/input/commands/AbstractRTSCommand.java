package org.voimala.myrts.screens.gameplay.input.commands;

public abstract class AbstractRTSCommand {

    protected int playerWhoMadeCommand;

    public AbstractRTSCommand(final int playerWhoMadeCommand) {
        this.playerWhoMadeCommand = playerWhoMadeCommand;
    }

    public abstract RTSCommandType getCommandName();

    public int getPlayerWhoMadeCommand() {
        return playerWhoMadeCommand;
    }
}
