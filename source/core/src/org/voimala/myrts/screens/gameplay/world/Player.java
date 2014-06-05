package org.voimala.myrts.screens.gameplay.world;

public class Player {

    private String name = "Unnamed Player";
    private int number = -1;
    private int team = -1;

    public Player() {

    }

    public Player(final String name, final int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Player number must be equal or greater than 0");
        }

        this.number = number;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(final int team) {
        if (team < 0) {
            throw new IllegalArgumentException("Team number must be equal or greater than 0");
        }

        this.team = team;
    }
}
