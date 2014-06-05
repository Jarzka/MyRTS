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
    private boolean running = true;
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
            running = false; // TODO Multiplayer lobby does not notice this?
        }
    }

    private void acceptConnections() {
        while (running) {
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
        connectedClients.add(client);

        assignSlotToPlayer(client);
        client.sendMessage(RTSProtocolManager.getInstance().createNetworkMessageOfTheDay(motd));

        client.start();
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
                slots.put(i, contentPlayer.toString());
                client.getPlayerInfo().setNumber(i);
                sendUpdatedSlotInfo();
                break;
            }
        }

        // Free slot not found?
        // TODO
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

        running = false;
        serverSocket.dispose();
    }

    public void removeClient(final ListenSocketThread listenSocketThread) {
        sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageChatMessage(serverChatName,
                listenSocketThread.getPlayerInfo().getName() + " " + "disconnected."));

        if (slots.get(listenSocketThread.getPlayerInfo().getNumber()) != null) {
            slots.put(listenSocketThread.getPlayerInfo().getNumber(), "OPEN");
        }

        connectedClients.remove(listenSocketThread);
        sendUpdatedSlotInfo();
    }

    public void sendUpdatedSlotInfo() {
        for (int i = 1; i <= NetworkManager.getInstance().SLOTS_MAX; i++) {
            if (slots.get(i).equals("OPEN")) {
                sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(i, slots.get(i)));
            } if (slots.get(i).equals("CLOSED")) {
                sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(i, slots.get(i)));
            } if (slots.get(i).startsWith("PLAYER|")) {
                ListenSocketThread client = findPlayerWhoPlaysInSlot(i);
                sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(i,
                        "PLAYER",
                        client.getPlayerInfo().getName(),
                        client.getPlayerInfo().getNetworkId()));
            } if (slots.get(i).equals("AI_TEST")) {
                sendMessageToAllClients(RTSProtocolManager.getInstance().createNetworkMessageSlotContent(i, slots.get(i)));
            }
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

    public String getServerChatName() {
        return serverChatName;
    }
}
