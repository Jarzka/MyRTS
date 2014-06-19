package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.ScreenName;
import org.voimala.myrts.screens.gameplay.world.WorldController;

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
        try {
            if (handleNetworkMessageMotd(message)
                    || handleNetworkMessageInputMoveUnit(message, listenSocketThread)
                    || handleNetworkMessageInputNoInput(message, listenSocketThread)
                    || handleNetworkMessageChat(message, listenSocketThread.getSocketType())
                    || handleNetworkMessagePing(message, listenSocketThread.getSocketType())
                    || handleNetworkMessagePong(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageSlot(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageNewConnectionInfo(message, listenSocketThread)
                    || handleNetworkMessageAdminRights(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageAdminStart(message, listenSocketThread)
                    || handleNetworkMessageStartGame(message, listenSocketThread.getSocketType())) {
                Gdx.app.debug(TAG, "Message handled successfully.");
                return true;
            } else {
                Gdx.app.debug(TAG, "WARNING: Unable to handle message: " + message);
                return false;
            }
        } catch (Exception e) {
            Gdx.app.debug(TAG, "WARNING: Network message caused an exception: " + e.getMessage());
            return false;
        }
    }

    private boolean handleNetworkMessageMotd(final String message) {
        if (message.startsWith("<MOTD|")) {
            String[] messageSplitted = splitNetworkMessage(message);
            ChatContainer.getInstance().addChatMessage(new ChatMessage("Server", messageSplitted[1], System.currentTimeMillis()));
            Gdx.app.debug(TAG, "Message of the day: " + messageSplitted[1]);
            return true;
        }

        return false;
    }

    /** Returns a string array which contains individual parts of the message.
     * Individual parts are separated by "|". "<" and ">" characters are removed. */
     public static String[] splitNetworkMessage(final String message) {
        String[] messageSplitted = message.split("\\|");
        // Remove < from the fist index
        messageSplitted[0] = messageSplitted[0].substring(1, messageSplitted[0].length());
        // Remove > from the last index
        messageSplitted[messageSplitted.length - 1] =
                messageSplitted[messageSplitted.length - 1].substring(0,
                        messageSplitted[messageSplitted.length - 1].length() - 1);

        return messageSplitted;
    }

    private boolean handleNetworkMessageInputMoveUnit(String message, final ListenSocketThread client) {
        if (message.startsWith("<INPUT|UNIT_MOVE|")) {
            if (client.getSocketType() == SocketType.SERVER_SOCKET) { // The message came from the server
                String messageSplitted[] = splitNetworkMessage(message);
                // TODO Check player number and that the player owns the selected unit.
                if (worldController != null) {
                    worldController.getGameplayScreen().getGameplayInputManager().addPlayerInputToQueue(message);
                }
            } else if (client.getSocketType() == SocketType.PLAYER_SOCKET) { // The message came to the server from a player
                ServerThread server = NetworkManager.getInstance().getServerThread();
                if (server != null) {
                    String messageSplitted[] = splitNetworkMessage(message);
                    server.sendMessageToAllClients(
                            RTSProtocolManager.getInstance().createNetworkMessageInputMoveUnit(
                                    Long.valueOf(messageSplitted[3]),
                                    Long.valueOf(messageSplitted[2]),
                                    client.getPlayerInfo().getNumber(),
                                    new Vector2(Float.valueOf(messageSplitted[4]),
                                            Float.valueOf(messageSplitted[5])))
                    );
                }

            }

            return true;
        }

        return false;
    }

    private boolean handleNetworkMessageInputNoInput(String message, final ListenSocketThread client) {
        if (message.startsWith("<INPUT|NO_INPUT|")) {
            if (client.getSocketType() == SocketType.SERVER_SOCKET) { // The message came from the server
                String messageSplitted[] = splitNetworkMessage(message);
                if (worldController != null) {
                    worldController.getGameplayScreen().getGameplayInputManager().addPlayerInputToQueue(message);
                }
            } else if (client.getSocketType() == SocketType.PLAYER_SOCKET) { // The message came to the server from a player
                ServerThread server = NetworkManager.getInstance().getServerThread();
                if (server != null) {
                    String messageSplitted[] = splitNetworkMessage(message);
                    server.sendMessageToAllClients(
                            RTSProtocolManager.getInstance().createNetworkMessageInputNoInput(
                                    Long.valueOf(messageSplitted[2]),
                                    client.getPlayerInfo().getNumber())
                    );
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
                ChatContainer.getInstance().addChatMessage(new ChatMessage(messageSplitted[1],
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
        return false; // TODO Implement ping protocol
    }

    private boolean handleNetworkMessagePong(final String message, final SocketType source) {
        return false; // TODO Implement ping protocol
    }

    private boolean handleNetworkMessageSlot(final String message, final SocketType source) {
        if (message.startsWith("<SLOT|")) {
            if (source == SocketType.SERVER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                 if (messageSplitted[2].equals("PLAYER")) {
                     // Update local player info first
                     if (GameMain.getInstance().getPlayer().getNetworkId() == (Integer.valueOf(messageSplitted[4]))) {
                         GameMain.getInstance().getPlayer().setNumber(Integer.valueOf(messageSplitted[1]));
                     }
                     LocalMultiplayerInfo.getInstance().getSlots().put(
                             Integer.valueOf(messageSplitted[1]),
                             messageSplitted[2] + "|" + messageSplitted[3] + "|" + messageSplitted[4]);
                 } else {
                     LocalMultiplayerInfo.getInstance().getSlots().put(
                             Integer.valueOf(messageSplitted[1]),
                             messageSplitted[2]);
                 }
                Gdx.app.debug(TAG, "Slot" + " " + messageSplitted[1] + " " + "content changed to" + " " + messageSplitted[2]);
                return true;
            }
        }

        return false;
    }

    private boolean handleNetworkMessageAdminRights(final String message, final SocketType source) {
        if (message.startsWith("<ADMIN_RIGHTS|")) {
            if (source == SocketType.SERVER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                if (messageSplitted[1].equals("GIVE")) {
                    Gdx.app.debug(TAG, "Admin rights have been given.");
                    GameMain.getInstance().getPlayer().setAdmin(true);
                } else if (messageSplitted[1].equals("REMOVE")) {
                    GameMain.getInstance().getPlayer().setAdmin(false);
                    Gdx.app.debug(TAG, "Admin rights have been removed.");
                }
                return true;
            }
        }

        return false;
    }

    private boolean handleNetworkMessageStartGame(final String message, final SocketType source) {
        if (message.equals("<COMMAND_AND_CONQUER>")) {
            Gdx.app.debug(TAG, "Starting game...");
            GameMain.getInstance().setNextScreen(ScreenName.LOAD_GAMEPLAY);
            return true;
        }

        return false;
    }

    private boolean handleNetworkMessageAdminStart(final String message, final ListenSocketThread client) {
        if (message.equals("<ADMIN|START>")) {
            if (client.getSocketType() == SocketType.PLAYER_SOCKET) {
                if (client.getPlayerInfo().isAdmin()) {
                    String messageSplitted[] = splitNetworkMessage(message);
                    if (messageSplitted[1].equals("START")) {
                        Gdx.app.debug(TAG, "Informing players that they can start the game.");
                        ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                        if (serverThread != null) {
                            serverThread.sendMessageToAllClients(createNetworkMessageStartGame());
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    private boolean handleNetworkMessageNewConnectionInfo(final String message, final ListenSocketThread listenSocketThread) {
        if (message.startsWith("<NEW_CONNECTION_INFO|")) {
            if (listenSocketThread.getSocketType() == SocketType.PLAYER_SOCKET) {
                // Update client info on the server
                String messageSplitted[] = splitNetworkMessage(message);
                listenSocketThread.getPlayerInfo().setName(messageSplitted[1]);
                listenSocketThread.getPlayerInfo().setNetworkId(Integer.valueOf(messageSplitted[2]));

                ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                if (serverThread != null) {
                    // Update slot info
                    serverThread.changeSlotContent(listenSocketThread.getPlayerInfo().getNumber(), "PLAYER" + "|" + listenSocketThread.getPlayerInfo().getName() + "|" + listenSocketThread.getPlayerInfo().getNetworkId());

                    // Send slots states to the connected player
                    for (int i = 1; i <= NetworkManager.getInstance().SLOTS_MAX; i++) {
                        listenSocketThread.sendMessage(createNetworkMessageSlotContent(
                                i,
                                serverThread.getSlots().get(i)));
                    }

                    // Inform other players
                    serverThread.sendMessageToAllClients(createNetworkMessageChatMessage(
                            serverThread.getServerChatName(),
                            messageSplitted[1] + " " + "connected."));

                }
            }

            return true;
        }

        return false;
    }

    public String createNetworkMessageInputMoveUnit(final long unitId,
                                                    final long simTick,
                                                    final Vector2 mouseLocationInWorld) {
        return "<INPUT|UNIT_MOVE|" + simTick + "|" + unitId + "|" + mouseLocationInWorld.x + "|" + mouseLocationInWorld.y + ">";
    }

    public String createNetworkMessageInputMoveUnit(final long unitId,
                                                    final long simTick,
                                                    final int playerNumber,
                                                    final Vector2 mouseLocationInWorld) {
        return "<INPUT|UNIT_MOVE|" + playerNumber + "|" + simTick + "|" + unitId + "|" + mouseLocationInWorld.x + "|" + mouseLocationInWorld.y + ">";
    }

    public String createNetworkMessageInputNoInput(final long simTick) {
        return "<INPUT|NO_INPUT|" + simTick + ">";
    }

    public String createNetworkMessageInputNoInput(final long simTick, final int playerNumber) {
        return "<INPUT|NO_INPUT|" + playerNumber + "|" + simTick + ">";
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

    public String createNetworkMessageGiveAdminRights() {
        return "<ADMIN_RIGHTS|GIVE>";
    }

    public String createNetworkMessageAdminStart() {
        return "<ADMIN|START>";
    }

    public String createNetworkMessageRemoveAdminRights() {
        return "<ADMIN_RIGHTS|REMOVE>";
    }

    public String createNetworkMessageStartGame() {
        return "<COMMAND_AND_CONQUER>";
    }

    public String createNetworkMessageSlotContent(final int slotNumber, final String content) {
        return createNetworkMessageSlotContent(slotNumber, content, "", 0);
    }

    /** @param playerName if content is PLAYER, this variable should contain the player's nick name. */
    public String createNetworkMessageSlotContent(final int slotNumber,
                                                  final String content,
                                                  final String playerName,
                                                  final int playerNetworkId) {

        return "<SLOT|" + String.valueOf(slotNumber) + "|" + content + ">";
    }

    public String createNetworkMessageNewConnectionInfo(final String nick, final int networkId) {
        return "<NEW_CONNECTION_INFO|" + nick + "|" + String.valueOf(networkId) + ">";
    }
}
