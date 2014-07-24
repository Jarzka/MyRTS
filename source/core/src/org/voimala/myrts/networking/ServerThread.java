package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread extends Thread {
    private static final String TAG = ServerThread.class.getName();
    private ServerSocketHints serverSocketHints;
    private ServerSocket serverSocket;
    private boolean serverRunning = true;
    private boolean gameRunning = false;
    private int port = 0;
    private ArrayList<ListenSocketThread> connectedClients = new ArrayList<ListenSocketThread>();
    /** Integer = slot number.
     * String = Slot state, which should be one of the following:
     * OPEN
     * CLOSED
     * TEST_AI
     * PLAYER|playerName|networkId
     * Slots 1-8 are meant for players, other slots are reserved for observers.
     * */
    private HashMap<Integer, String> slots = new HashMap<Integer, String>();
    private String motd = "Welcome to the server!";
    private String serverChatName = "Server";
    /** The first string is in the following format: SimTick_PlayerNumber, the second string contains the Hash */
    private HashMap<String, String> playerGameStateHashes = new HashMap<String, String>();

    public ServerThread(final int port) {
        super(ServerThread.class.getName());

        initializeGameSlots();
        initializeSocketSettings(port);
    }

    private void initializeGameSlots() {
        for (int i = 1; i <= NetworkManager.getInstance().SLOTS_MAX; i++) {
            slots.put(i, "OPEN");
        }
    }

    private void initializeSocketSettings(int port) {
        serverSocketHints = new ServerSocketHints();
        serverSocketHints.acceptTimeout = 100000;
        serverSocketHints.receiveBufferSize = 90000;
        this.port = port;
    }

    public void run() {
        NetworkManager.getInstance().setHost(true);

        createServer();
        acceptConnections();

        Gdx.app.debug(TAG, "Server stopped.");
        NetworkManager.getInstance().setHost(false);
    }

    private void createServer() {
        try {
            Gdx.app.debug(TAG, "Creating a server...");
            serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, serverSocketHints);
            Gdx.app.debug(TAG, "Server created");
        } catch (Exception e) {
            Gdx.app.debug(TAG, "Error creating a server: " + e.getMessage());
            serverRunning = false;
        }
    }

    private void acceptConnections() {
        while (serverRunning) {
            try {
                Gdx.app.debug(TAG, "Listening connections...");
                SocketHints socketHints = new SocketHints();
                socketHints.connectTimeout = 10000;
                socketHints.receiveBufferSize = 90000;
                socketHints.sendBufferSize = 90000;
                socketHints.tcpNoDelay = true;
                Socket clientSocket = serverSocket.accept(socketHints);
                Gdx.app.debug(TAG, "Client connected from" + " " + clientSocket.getRemoteAddress());
                handleNewClientConnection(clientSocket);
            } catch (Exception e) {
                Gdx.app.debug(TAG, "Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private void handleNewClientConnection(final Socket clientSocket) {
        ListenSocketThread client = new ListenSocketThread(this, clientSocket);

        checkIfGameIsRunning(client);
        assignSlotToPlayer(client);
        connectedClients.add(client);
        client.sendMessage(RTSProtocolManager.getInstance().createNetworkMessageOfTheDay(motd));
        handleAdminRights(client);

        client.start();
    }

    private void checkIfGameIsRunning(final ListenSocketThread client) {
        if (gameRunning) {
            kickClient(client, "Not allowed to join while the game is running.");
        }
    }

    private void assignSlotToPlayer(ListenSocketThread client) {
        // Find the next free slot
        for (int i = 1; i <= NetworkManager.getInstance().SLOTS_MAX; i++) {
            if (slots.get(i).equals("OPEN")) {
                StringBuilder contentPlayer = new StringBuilder();
                contentPlayer.append("PLAYER");
                contentPlayer.append("|");
                contentPlayer.append(client.getPlayerInfo().getName());
                contentPlayer.append("|");
                contentPlayer.append(client.getPlayerInfo().getNetworkId());
                changeSlotContent(i, contentPlayer.toString());
                client.getPlayerInfo().setNumber(i);
                sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(
                        i,
                        slots.get(i)));
                break;
            }
        }

        // Free slot not found?
        kickClient(client, "Server is full");
    }

    private void kickClient(final ListenSocketThread client, final String message) {
        // TODO Implement "kick" in protocol
    }

    /** Gives admin rights to the client if he is the first player in the game */
    private void handleAdminRights(ListenSocketThread client) {
        if (connectedClients.size() == 1) {
            client.getPlayerInfo().setAdmin(true);
            client.sendMessage(RTSProtocolManager.getInstance().createNetworkMessageGiveAdminRights());
            client.sendMessage(RTSProtocolManager.getInstance().createNetworkMessageChatMessage(serverChatName, "You are now admin."));
        }
    }

    public void sendMessageToAllClients(final String message) {
        for (ListenSocketThread client : connectedClients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                Gdx.app.debug(TAG, "WARNING: Unable to send message to client" + " "
                        + client.getPlayerInfo().getName() + ". " + e.getMessage());
            }
        }
    }

    public void die() {
        for (ListenSocketThread client : connectedClients) {
            client.die();
        }

        serverRunning = false;
        gameRunning = false;
        if (serverSocket != null) {
            serverSocket .dispose();
        }
    }

    public void removeClient(final ListenSocketThread listenSocketThread) {
        sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageChatMessage(serverChatName,
                listenSocketThread.getPlayerInfo().getName() + " " + "disconnected."));

        changeSlotContent(listenSocketThread.getPlayerInfo().getNumber(), "OPEN");
        connectedClients.remove(listenSocketThread);
    }

    /** Changed the slot content and sends the info to the players. */
    public void changeSlotContent(final int slotNumber, final String content) {
        if (slots.get(slotNumber) != null) {
            slots.put(slotNumber, content);
            sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(
                    slotNumber,
                    slots.get(slotNumber)));
        }
    }

    /** Returns null if player is not found. */
    private ListenSocketThread findPlayerWhoPlaysInSlot(final int slot) {
        for (ListenSocketThread client : connectedClients) {
            if (client.getPlayerInfo().getNumber() == slot) {
                return client;
            }
        }

        return null;
    }

    // Stores the given hash in memory and checks if hashes match for this simTick.
    public void addAndCheckGameStateHashes(final int playerNumber, final long simTick, final String hash) {
        playerGameStateHashes.put(simTick + "_" + playerNumber, hash);
        checkHashesMatchForSimTick(simTick);
    }

    private void checkHashesMatchForSimTick(final long simTick) {
        for (int i = 1; i <= 8; i++) {
            if (!slots.get(i).startsWith("PLAYER")) {
                continue;
            }

            if (playerGameStateHashes.get(simTick + "_" + i) == null) {
                continue; // This player has not sent his hash for this simtick (yet)
            }

            // Get this player's hash for the current simTick
            String playerHash = playerGameStateHashes.get(simTick + "_" + i);

            // Compare it to other hashes
            for (int j = 1; j <= 8; j++) {
                if (playerGameStateHashes.get(simTick + "_" + j) == null) {
                    continue; // The other player has not sent his hash for this simtick (yet)
                }

                String otherHash = playerGameStateHashes.get(simTick + "_" + j);
                if (!playerHash.equals(otherHash)) {
                    Gdx.app.debug(TAG, "WARNING! at simtick " + simTick + " player " + i + " hash:\n" + playerHash + "\nis not the same as player " + j
                            + " hash:\n" + otherHash);
                    sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageChatMessage(serverChatName,
                            "WARNING! GAME IS OUT OF SYNC AT SIMTICK " + simTick + "!"));
                }
            }
        }
    }

    public String getServerChatName() {
        return serverChatName;
    }

    public HashMap<Integer, String> getSlots() {
        return slots;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }
}
