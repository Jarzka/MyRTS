package org.voimala.myrts.screens.gameplay.input;

public class PlayerInput {

    private int simTick = 0;
    private int playerNumber = 0;
    private String input;

    public PlayerInput(final int simTick, final int playerNumber, final String input) {
        this.simTick = simTick;
        this.playerNumber = playerNumber;
        this.input = input;
    }

    public int getSimTick() {
        return simTick;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getInput() {
        return input;
    }
}
