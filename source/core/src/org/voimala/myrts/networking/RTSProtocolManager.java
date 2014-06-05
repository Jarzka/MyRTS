package org.voimala.myrts.networking;

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
                || handleNetworkMessagePong(message, source)
                || handleNetworkMessageSlot(message, source);
    }

    private boolean handleNetworkMessageMotd(final String message) {
        if (message.startsWith("<MOTD|")) {
            String[] messageSplitted = splitNetworkMessage(message);
            Chat.getInstance().addChatMessage(new ChatMessage("Server", messageSplitted[1], System.currentTimeMillis()));
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
        if (message.startsWith("<CHAT|")) {
            String[] messageSplitted = splitNetworkMessage(message);
            Chat.getInstance().addChatMessage(new ChatMessage(messageSplitted[1],
                    messageSplitted[2],
                    System.currentTimeMillis()));
            Gdx.app.debug(TAG, messageSplitted[1] + ": " + messageSplitted[2]);
            return true;
        }

        return false;
    }

    private boolean handleNetworkMessagePing(final String message, final SocketType source) {
        return false; // TODO
    }

    private boolean handleNetworkMessagePong(final String message, final SocketType source) {
        return false; // TODO
    }

    private boolean handleNetworkMessageSlot(final String message, final SocketType source) {
        if (message.startsWith("<SLOT|")) {
            if (source == SocketType.SERVER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                GameMain.getInstance().getPlayer().setNumber(Integer.valueOf(messageSplitted[1]));
                Gdx.app.debug(TAG, "This player plays now on slot" + " " + messageSplitted[1]);
                return true;
            }
        }

        return false;
    }

    public String createNetworkMessageMoveUnit(String unitId,
                                               final Vector3 mouseLocationInWorld) {
        // TODO Use StringBuilder in send methods
        return "<UNIT_MOVE|" + unitId + "|" + mouseLocationInWorld.x + "|" + mouseLocationInWorld.y + ">";
    }

    public String createNetworkMessageChatMessage(final String nick, final String message) {
        return "<CHAT|" + nick + "|" + message + ">";
    }

    public String createNetworkMessagePing() {
        return "<PING>";
    }

    public String createNetworkMessagePong() {
        return "<PONG>";
    }

    public String createNetworkMessageOfTheDay(final String motd) {
        return "<MOTD|" + motd + ">";
    }

    public String createNetworkMessageSlot(final int slot) {
        return "<SLOT|" + String.valueOf(slot) + ">";
    }
}
