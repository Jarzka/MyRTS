package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.units.Unit;

/** This class is used to send network messages that respect the game's protocol.
 *
 * Protocol
 *
 * Message template
 * <MESSAGE_TYPE|parameter1|parameter2|...>
 *
 */

public class RTSProtocolManager {

    private static final String TAG = RTSProtocolManager.class.getName();
    private static RTSProtocolManager instanceOfThis = null;
    private WorldController worldController;

    private RTSProtocolManager() {
        Gdx.app.setLogLevel(GameMain.LOG_LEVEL);
    }

    public static RTSProtocolManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new RTSProtocolManager();
        }

        return instanceOfThis;
    }

    public void setWorldController(WorldController worldController) {
        this.worldController = worldController;
    }

    public boolean handleNetworkMessage(final String message, final SocketType source) {
        return handleNetworkMessageMotd(message)
                || handleNetworkMessageMoveUnit(message, source)
                || handleNetworkMessageChat(message, source)
                || handleNetworkMessagePing(message, source)
                || handleNetworkMessagePong(message, source);
    }

    private boolean handleNetworkMessageMotd(final String message) {
        if (message.startsWith("<MOTD|")) {
            String[] messageSplitted = splitNetworkMessage(message);
            Gdx.app.debug(TAG, "Message of the day: " + messageSplitted[1]);
            return true;
        }

        return false;
    }

    /** Returns a string array which contains individual parts of the message.
     * Individual parts are separated by "|". "<" and ">" characters are removed. */
    private String[] splitNetworkMessage(final String message) {
        String[] messageSplitted = message.split("\\|");
        // Remove < from the fist index
        messageSplitted[0] = messageSplitted[0].substring(1, messageSplitted[0].length());
        // Remove > from the last index
        messageSplitted[messageSplitted.length - 1] =
                messageSplitted[messageSplitted.length - 1].substring(0,
                        messageSplitted[messageSplitted.length - 1].length() - 1);

        return messageSplitted;
    }

    private boolean handleNetworkMessageMoveUnit(String message, final SocketType source) {
        if (message.startsWith("<UNIT_MOVE|")) {
            if (source == SocketType.SERVER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                if (worldController != null) {
                    Unit unit = worldController.findUnitById(messageSplitted[1]);
                    if (unit != null) {
                        unit.getMovement().setPathPoint(
                                new Vector2(Float.valueOf(messageSplitted[2]),
                                        Float.valueOf(messageSplitted[3])));
                    }
                }
            } else if (source == SocketType.PLAYER_SOCKET) {
                ServerThread server = NetworkManager.getInstance().getServerThread();
                if (server != null) {
                    server.sendMessageToAllClients(message);
                }

            }

            return true;
        }

        return false;
    }

    private boolean handleNetworkMessageChat(final String message, final SocketType source) {
        return false; // TODO
    }

    private boolean handleNetworkMessagePing(final String message, final SocketType source) {
        return false; // TODO
    }

    private boolean handleNetworkMessagePong(final String message, final SocketType source) {
        return false; // TODO
    }


    public String generateMessageMoveUnit(final ClientThread client,
                                          String unitId,
                                          final Vector3 mouseLocationInWorld) {
        // TODO Connection lost?
        // TODO Use StringBuilder in send methods
        return "<UNIT_MOVE|" + unitId + "|" + mouseLocationInWorld.x + "|" + mouseLocationInWorld.y + ">";
    }

    public String generateMessageChatMessage(final ClientThread client, final String nick, final String message) {
        return "<CHAT|" + nick + "|" + message + ">";
    }

    public void sendMessage(final ClientThread client, final String message) {
        // TODO Connection lost?
        try {
            client.sendMessage(message);
        } catch (Exception e) {
            Gdx.app.debug(TAG, "WARNING: Unable to send move unit command to the network: " + e.getMessage());
        }
    }

    public String generateMessagePing(final ClientThread client) {
        return "<PING>";
    }

    public String generateMessagePong(final ClientThread client) {
        return "<PONG>";
    }

    public String generateMessageOfTheDay(ClientThread client) {
        return "<MOTD|Welcome to the server.>"; // TODO Hardcoded.
    }
}
