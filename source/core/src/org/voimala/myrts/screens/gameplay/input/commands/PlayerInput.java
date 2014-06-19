package org.voimala.myrts.screens.gameplay.input.commands;

/** This class represents an command that a player has made during gameplay. */
public class PlayerInput {

    private int simTick = 0; /** SimTick that was active when the player performed the command. */
    private int playerNumber = 0; /** Player who performed the command. */
    private AbstractRTSCommand command;

    public PlayerInput(final int simTick, final int playerNumber, final AbstractRTSCommand command) {
        this.simTick = simTick;
        this.playerNumber = playerNumber;
        this.command = command;
    }

    public int getSimTick() {
        return simTick;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public AbstractRTSCommand getCommand() {
        return command;
    }
}
