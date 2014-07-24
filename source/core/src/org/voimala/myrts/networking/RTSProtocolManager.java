package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.exceptions.UnableToHandleNetworkMessage;
import org.voimala.myrts.screens.ScreenName;
import org.voimala.myrts.screens.gameplay.input.NetworkInputQueue;
import org.voimala.myrts.screens.gameplay.input.PlayerInput;
import org.voimala.myrts.screens.gameplay.input.commands.AbstractRTSCommand;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandEmpty;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandMoveUnit;
import org.voimala.myrts.screens.gameplay.input.commands.RTSCommandType;
import org.voimala.myrts.screens.gameplay.world.WorldController;

import java.util.ArrayList;
import java.util.List;

/** This class is used to sendInputsToOtherPlayers network messages that respect the game's protocol. */

// RTS Extract this class
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
                    || handleNetworkMessageInput(message, listenSocketThread)
                    || handleNetworkMessageChat(message, listenSocketThread.getSocketType())
                    || handleNetworkMessagePing(message, listenSocketThread.getSocketType())
                    || handleNetworkMessagePong(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageSlot(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageNewConnectionInfo(message, listenSocketThread)
                    || handleNetworkMessageAdminRights(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageAdminStart(message, listenSocketThread)
                    || handleNetworkMessageStartGame(message, listenSocketThread.getSocketType())
                    || handleNetworkMessageGameStateHash(message, listenSocketThread)) {
                return true;
            } else {
                Gdx.app.debug(TAG, "WARNING: Unable to handle message: " + message);
                return false;
            }
        } catch (Exception e) {
            Gdx.app.debug(TAG, "WARNING: An exception was thrown while reading network message: " + e.getMessage());
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

    /** Returns a string array which contains individual inputs in the of the message.
     * Individual parts are separated by "|". "<" and ">" characters are removed.
     * For example the following message:
     * <THIS|IS|MESSAGE>
     * will be splitted:
     * 0 THIS
     * 1 IS
     * 2 MESSAGE */
     public String[] splitNetworkMessage(final String message) {
        String[] messageSplitted = message.split("\\|");
        // Remove < from the fist index
        messageSplitted[0] = messageSplitted[0].substring(1, messageSplitted[0].length());
        // Remove > from the last index
        messageSplitted[messageSplitted.length - 1] =
                messageSplitted[messageSplitted.length - 1].substring(0,
                        messageSplitted[messageSplitted.length - 1].length() - 1);

        return messageSplitted;
    }

    private boolean handleNetworkMessageInput(final String message, final ListenSocketThread client) {
        if (message.startsWith("<INPUT|")) {
            if (client.getSocketType() == SocketType.SERVER_SOCKET) { // The message came from the server
                String messageSplitted[] = splitNetworkMessage(message);
                /* Convert message to PlayerInput and add it to the queue. MultiplayerSynchronizationManager
                 * will execute the inputs at the right SimTick. */
                List<PlayerInput> playerInputs = RTSProtocolManager.getInstance().createPlayerInputFromNetworkMessage(
                        Long.valueOf(messageSplitted[1]),
                        Integer.valueOf(messageSplitted[2]),
                        messageSplitted[3]);
                for (PlayerInput playerInput : playerInputs) {
                    NetworkInputQueue.getInstance().addPlayerInputToQueue(playerInput);
                }
            } else if (client.getSocketType() == SocketType.PLAYER_SOCKET) { // The message came to the server from a player
                // Append the player number to the message and send it to other players
                ServerThread server = NetworkManager.getInstance().getServerThread();
                if (server != null) {
                    String messageSplitted[] = splitNetworkMessage(message);
                    server.sendMessageToAllClients(
                            RTSProtocolManager.getInstance().createNetworkMessagePlayerInputsToBeSentForOtherPlayers(
                                Long.valueOf(messageSplitted[1]),
                                client.getPlayerInfo().getNumber(),
                                messageSplitted[2]));
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
                    Gdx.app.debug(TAG, "Informing players that they can start the game.");
                    ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                    if (serverThread != null) {
                        serverThread.setGameRunning(true);
                        serverThread.sendMessageToAllClients(createNetworkMessageStartGame());
                    }
                }
            }

            return true;
        }

        return false;
    }

    private boolean handleNetworkMessageGameStateHash(final String message, final ListenSocketThread client) {
        if (message.startsWith("<HASH|")) {
            if (client.getSocketType() == SocketType.PLAYER_SOCKET) {
                String messageSplitted[] = splitNetworkMessage(message);
                ServerThread serverThread = NetworkManager.getInstance().getServerThread();
                if (serverThread != null) {
                    serverThread.addAndCheckGameStateHashes(
                            client.getPlayerInfo().getNumber(),
                            Long.valueOf(messageSplitted[1]),
                            messageSplitted[2]);
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


    /** Creates a network message from PlayerInput objects, for example:
     * <INPUT|4|[UNIT_MOVE|5|300|200][IUNIT_ATTACK|6|80]> */
    public String createNetworkMessageFromPlayerInputs(List<PlayerInput> playerInputs, long simTick) {
        String inputMessage = "<INPUT|" + simTick + "|";

        for (PlayerInput playerInput : playerInputs) {
            if (playerInput.getCommand().getCommandName() == RTSCommandType.EMPTY) {
                inputMessage += RTSProtocolManager.getInstance().createNetworkMessageInputNoInput();
            } else if (playerInput.getCommand().getCommandName() == RTSCommandType.MOVE_UNIT) {
                RTSCommandMoveUnit moveUnitCommand = (RTSCommandMoveUnit) playerInput.getCommand();
                inputMessage += RTSProtocolManager.getInstance().createNetworkMessageInputMoveUnit(
                        moveUnitCommand.getObjectId(),
                        moveUnitCommand.getTargetPosition());
            }
        }


        inputMessage += ">";

        return inputMessage;
    }

    /**
     * @param playerInputs Player inputs, for example: [UNIT_MOVE?5?300?200][UNIT_ATTACK?6?80]
     * @return
     */
    public String createNetworkMessagePlayerInputsToBeSentForOtherPlayers(final long simTick,
                                                                          final int playerNumber,
                                                                          final String playerInputs) {
        return "<INPUT|" + simTick + "|" + playerNumber + "|" + playerInputs + ">";
    }

    public String createNetworkMessageInputMoveUnit(final long unitId,
                                                    final Vector2 targetPosition) {
        return "[UNIT_MOVE?" + unitId + "?" + targetPosition.x + "?" + targetPosition.y + "]";
    }

    public String createNetworkMessageInputNoInput() {
        return "[NO_INPUT]";
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

    public String createNetworkMessageGameStateHash(final long simTick, final String hash) {
        return "<HASH|" + simTick + "|" + hash + ">";
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

    /**
     * @param networkMessageInputs Inputs that the player sent, for example: [UNIT_MOVE-5-300-200][UNIT_ATTACK-6-80]
     */
    private List<PlayerInput> createPlayerInputFromNetworkMessage(final long simTick,
                                                            final int playerNumber,
                                                            final String networkMessageInputs) {
        ArrayList<PlayerInput> playerInputs = new ArrayList<PlayerInput>();
        for (AbstractRTSCommand command : createRTSCommandsFromNetworkInputMessage(playerNumber, networkMessageInputs)) {
            playerInputs.add(new PlayerInput(playerNumber, simTick, command));
        }

        return playerInputs;
    }

    /**
     * @param networkMessageInputs Inputs that the player sent, for example: [UNIT_MOVE?5?300?200][UNIT_ATTACK?6?80]
     */
    private List<AbstractRTSCommand> createRTSCommandsFromNetworkInputMessage(final int playerNumber, final String networkMessageInputs) {
        ArrayList<AbstractRTSCommand> commands = new ArrayList<AbstractRTSCommand>();

        // Read individual input
        StringBuilder readSingleInput = new StringBuilder();
        int i = 0;
        while(true) {
            try {
                char character = networkMessageInputs.charAt(i);
                readSingleInput.append(character);
                i++;

                if (character == ']') {
                    // End of individual input reached.
                    commands.add(createRTSCommandFromIndividualNetworkInputMessage(playerNumber, readSingleInput.toString()));
                    readSingleInput = new StringBuilder();
                }
            } catch (Exception e) {
                // End of message reached
                break;
            }
        }

        return commands;
    }

    /**
     * @param individualNetworkInput Individual network input, for example: [UNIT_MOVE?5?300?200]
     */
    private AbstractRTSCommand createRTSCommandFromIndividualNetworkInputMessage(final int playerNumber, final String individualNetworkInput) {
        String[] networkMessageInputsSplitted = splitNetworkMessageInput(individualNetworkInput);

        if (networkMessageInputsSplitted[0].equals("UNIT_MOVE")) {
            return createRTSCommandMoveUnitFromNetworkInputMessage(playerNumber, individualNetworkInput);
        } else if (networkMessageInputsSplitted[0].equals("NO_INPUT")) {
            return createRTSCommandNoInputFromNetworkInputMessage(playerNumber);
        }

        throw new UnableToHandleNetworkMessage("Player input type was unknown: " + networkMessageInputsSplitted[0]);
    }

    /** Returns a string array which contains individual inputs in the of the message.
     * Individual parts are separated by "?". "[" and "]" characters are removed.
     * For example the following message:
     * [THIS?IS?MESSAGE]
     * will be splitted:
     * 0 THIS
     * 1 IS
     * 2 MESSAGE */
    public String[] splitNetworkMessageInput(final String message) {
        String[] messageSplitted = message.split("\\?");
        // Remove [ from the fist index
        messageSplitted[0] = messageSplitted[0].substring(1, messageSplitted[0].length());
        // Remove ] from the last index
        messageSplitted[messageSplitted.length - 1] =
                messageSplitted[messageSplitted.length - 1].substring(0,
                        messageSplitted[messageSplitted.length - 1].length() - 1);

        return messageSplitted;
    }

    private RTSCommandMoveUnit createRTSCommandMoveUnitFromNetworkInputMessage(final int playerNumber, final String individualNetworkInput) {
        String[] networkMessageInputsSplitted = splitNetworkMessageInput(individualNetworkInput);

        return new RTSCommandMoveUnit(playerNumber, Long.valueOf(networkMessageInputsSplitted[1]),
                new Vector2(Float.valueOf(networkMessageInputsSplitted[2]),
                        Float.valueOf(networkMessageInputsSplitted[3])));

    }

    private AbstractRTSCommand createRTSCommandNoInputFromNetworkInputMessage(final int playerNumber) {
        return new RTSCommandEmpty(playerNumber);
    }

}
