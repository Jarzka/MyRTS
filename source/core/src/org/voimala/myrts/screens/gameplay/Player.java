package org.voimala.myrts.screens.gameplay;

public class Player {

    private String name;
    private int number;
    private int team;

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
