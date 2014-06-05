package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.gameplay.world.WorldController;
import org.voimala.myrts.screens.gameplay.units.Unit;

/** This class is used to send network messages that respect the game's protocol. */

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

    public boolean handleNetworkMessage(final String message, final ListenSocketThread listenSocketThread) {
        return handleNetworkMessageMotd(message)
                || handleNetworkMessageMoveUnit(message, listenSocketThread.getSocketType())
                || handleNetworkMessageChat(message, listenSocketThread.getSocketType())
                || handleNetworkMessagePing(message, listenSocketThread.getSocketType())
                || handleNetworkMessagePong(message, listenSocketThread.getSocketType())
                || handleNetworkMessageSlot(message, listenSocketThread.getSocketType())
                || handleNetworkMessageNewConnectionInfo(message, listenSocketThread);
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
            if (source == SocketType.SERVER_SOCKET) { // The message came from the server
                String messageSplitted[] = splitNetworkMessage(message);
                if (worldController != null) {
                    Unit unit = worldController.findUnitById(messageSplitted[1]);
                    if (unit != null) {
                        unit.getMovement().setPathPoint(
                                new Vector2(Float.valueOf(messageSplitted[2]),
                                        Float.valueOf(messageSplitted[3])));
                    }
                }
            } else if (source == SocketType.PLAYER_SOCKET) { // The message came to the server from a player
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
            if (source == SocketType.SERVER_SOCKET) {
                String[] messageSplitted = splitNetworkMessage(message);
                Chat.getInstance().addChatMessage(new ChatMessage(messageSplitted[1],
                        messageSplitted[2],
                        System.currentTimeMillis()));
                Gdx.app.debug(TAG, messageSplitted[1] + ": " + messageSplitted[2]);
            } else if (source == SocketType.PLAYER_SOCKET) {
                ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                if (serverThread != null) {
                    serverThread.sendMessageToAllClients(message);
                }
            }

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
                Gdx.app.debug(TAG, "Slot" + " " + messageSplitted[1] + " " + "content changed to" + " " + messageSplitted[2]);
                return true;
            }
        }

        return false;
    }

    private boolean handleNetworkMessageNewConnectionInfo(final String message, final ListenSocketThread listenSocketThread) {
        if (message.startsWith("<NEW_CONNECTION_INFO|")) {
            if (listenSocketThread.getSocketType() == SocketType.PLAYER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                listenSocketThread.getPlayerInfo().setName(messageSplitted[1]);

                ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                if (serverThread != null) {
                    serverThread.updateSlots();
                    serverThread.sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageChatMessage(
                            serverThread.getServerChatName(),
                            messageSplitted[1] + " " + "connected."));

                }

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

    public String createNetworkMessageSlotContent(final int slotNumber, final String content) {
        return createNetworkMessageSlotContent(slotNumber, content, "");
    }

    public String createNetworkMessageNewConnectionInfo(final String nick) {
        return "<NEW_CONNECTION_INFO|" + nick + ">";
    }

    /** @param playerName if content is PLAYER, this variable should contain the player's nick name. */
    public String createNetworkMessageSlotContent(final int slotNumber, final String content, final String playerName) {
        StringBuilder buildMessage = new StringBuilder();
        buildMessage.append("<SLOT|");
        buildMessage.append(String.valueOf(slotNumber));
        buildMessage.append("|");
        buildMessage.append(content);

        if (content.equals("PLAYER")) {
            buildMessage.append("|");
            buildMessage.append(playerName);
        }

        buildMessage.append(">");

        return buildMessage.toString();
    }
}
