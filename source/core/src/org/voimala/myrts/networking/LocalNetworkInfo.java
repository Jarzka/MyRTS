package org.voimala.myrts.networking;

import java.util.HashMap;

/** This class contains local information about the game network game. Things like player names,
 * current map name etc. are kept in this class. */
public class LocalNetworkInfo {

    private static LocalNetworkInfo instanceOfThis;

    /** This should be identical to the server's hash map. See: ServerThreads class.*/
    private HashMap<Integer, String> slots = new HashMap<Integer, String>();
    private String mapName;

    private LocalNetworkInfo() {
        initialize();
    }

    private void initialize() {
        for (int i = 1; i <= NetworkManager.getInstance().SLOTS_MAX; i++) {
            slots.put(i, "OPEN");
        }
    }

    public static LocalNetworkInfo getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new LocalNetworkInfo();
        }

        return instanceOfThis;
    }

    public HashMap<Integer, String> getSlots() {
        return slots;
    }
}
