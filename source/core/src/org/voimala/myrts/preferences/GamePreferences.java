package org.voimala.myrts.preferences;

public class GamePreferences {
    private static GamePreferences instanceOfThis;

    public static GamePreferences getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new GamePreferences();

        }
        return instanceOfThis;
    }

    private GamePreferences() {
    }
}
