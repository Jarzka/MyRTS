package org.voimala.myrts.screens.gameplay.world;

import org.voimala.utility.RandomNumberGenerator;

public class Player {

    /// Every player should have an unique network id. Get number between 0 and Integer max value.
    private int networkId = RandomNumberGenerator.random(0, (int) (Math.pow(2, 31) - 2));
    private String name = "Player" + RandomNumberGenerator.random(0, 10000);
    private int number = -1; /// Tells in which slot is player is playing
    private int team = -1;
    private boolean isAdmin = false;

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

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
