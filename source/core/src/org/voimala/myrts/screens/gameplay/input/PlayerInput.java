package org.voimala.myrts.screens.gameplay.input;

import org.voimala.myrts.screens.gameplay.input.commands.AbstractRTSCommand;

/** This class represents an command that a player has made during gameplay. */
public class PlayerInput {

    private long simTick = 0; /** SimTick that was active when the player performed the command. */
    private int playerNumber = 0; /** Player who performed the command. */
    private AbstractRTSCommand command;

    public PlayerInput(final int playerNumber, final long simTick, final AbstractRTSCommand command) {
        this.simTick = simTick;
        this.playerNumber = playerNumber;
        this.command = command;
    }

    public long getSimTick() {
        return simTick;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public AbstractRTSCommand getCommand() {
        return command;
    }
}
