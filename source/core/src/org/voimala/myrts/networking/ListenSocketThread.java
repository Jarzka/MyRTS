package org.voimala.myrts.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.voimala.myrts.app.GameMain;
import org.voimala.myrts.screens.gameplay.world.Player;

import java.io.InputStreamReader;

public class ListenSocketThread extends Thread {

    private static final String TAG = ListenSocketThread.class.getName();
    private SocketHints socketHints;
    private int port = 0;
    private String ip;
    private Socket socket;
    private boolean running = true;
    private ConnectionState connectionState = ConnectionState.NOT_CONNECTED;

    private ServerThread serverThread;

    private Player player;
    private SocketType socketType;

    /** Used when the server creates a new thread for the connected client. */
    public ListenSocketThread(final ServerThread serverThread, final Socket socket) {
        super(ListenSocketThread.class.getName());

        this.serverThread = serverThread;
        this.socket = socket;
        this.socketType = SocketType.PLAYER_SOCKET;
        player = new Player();
    }

    /** Used for connecting to the server. */
    public ListenSocketThread(final String ip, final int port) {
        super(ListenSocketThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;
        socketHints.receiveBufferSize = 90000;
        socketHints.sendBufferSize = 90000;
        socketHints.tcpNoDelay = true;
        this.ip = ip;
        this.port = port;
        this.socketType = SocketType.SERVER_SOCKET;
        player = new Player();
    }

    public void run() {
        if (socketType == SocketType.SERVER_SOCKET) {
            connectToTheServer();
        } else if (socketType == SocketType.PLAYER_SOCKET) {
            // The socket is already connected to a player
            connectionState = ConnectionState.CONNECTED;
        }

        if (socket != null) {
            if (socket.isConnected()) {
                listenNetworkMessagesUntilDisconnected();
            }
        }

        handleDisconnection();
    }

    private void listenNetworkMessagesUntilDisconnected() {
        if (socketType == SocketType.SERVER_SOCKET) {
            Gdx.app.debug(TAG, "Listening messages from the server.");
        } else if (socketType == SocketType.PLAYER_SOCKET) {
            Gdx.app.debug(TAG, "Listening messages from the player.");
        }

        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        char[] readCharacter = new char[1]; // Read character will be stored in this array
        StringBuilder constructMessage = new StringBuilder();

        while (running) {
            try {
                // Read one character from buffer or wait until there is a message in the buffer
                if (inputStream.read(readCharacter) == -1) {
                    // Socket disconnected.
                    running = false;
                } else {
                    constructMessage.append(readCharacter[0]);

                    // Message is too long?
                    if (constructMessage.length() > NetworkManager.getInstance().NETWORK_MESSAGE_MAX_LENGTH_CHARACTERS) {
                        constructMessage = new StringBuilder();
                        Gdx.app.debug(TAG, "WARNING: Network message was too long and was rejected!");
                    }

                    if (readCharacter[0] == '>') { // End of the message reached, handle message
                        if (socketType == SocketType.SERVER_SOCKET) {
                            Gdx.app.debug(TAG, "Got message from the server: " + constructMessage);
                        } else if (socketType == SocketType.PLAYER_SOCKET) {
                            Gdx.app.debug(TAG, "Got message from the player " + player.getNumber() + ": " + constructMessage);
                        }

                        RTSProtocolManager.getInstance().handleNetworkMessage(constructMessage.toString(),
                                this);
                        constructMessage = new StringBuilder();
                    }
                }
            } catch (Exception e) {
                Gdx.app.debug(TAG, "ERROR: while reading buffer: " + e.getMessage());
                running = false;
            }
        }
    }

    private void handleDisconnection() {
        connectionState = ConnectionState.NOT_CONNECTED;
        if (socketType == SocketType.PLAYER_SOCKET && serverThread != null) {
            serverThread.removeClient(this);
        }

        if (socket != null) {
            this.socket.dispose();
        }

        Gdx.app.debug(TAG, "Socket disconnected.");
    }

    private void connectToTheServer() {
        this.connectionState = ConnectionState.CONNECTING_TO_THE_SERVER;

        while(running) {
            try {
                Gdx.app.debug(TAG, "Connecting to the server...");
                socket = Gdx.net.newClientSocket(Net.Protocol.TCP, ip, port, socketHints);
                connectionState = ConnectionState.CONNECTED;
                Gdx.app.debug(TAG, "Connected to the server.");
                sendMessage(RTSProtocolManager.getInstance().createNetworkMessageNewConnectionInfo(
                        GameMain.getInstance().getPlayer().getName(),
                        GameMain.getInstance().getPlayer().getNetworkId()));
                Gdx.app.debug(TAG, "Sending player information.");
                break;
            } catch (Exception e) {
                connectionState = ConnectionState.NOT_CONNECTED;
                Gdx.app.debug(TAG, "Could not connect to the server: " + e.getMessage());
                Gdx.app.debug(TAG, "Trying again in a few seconds...");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // Continue
                }
            }
        }

    }

    public void sendMessage(final String message) {
        try {
            if (socketType == SocketType.SERVER_SOCKET) {
                Gdx.app.debug(TAG, "Player " + GameMain.getInstance().getPlayer().getNumber() + ": Sending message to the server: " + message);
            } else if (socketType == SocketType.PLAYER_SOCKET) {
                Gdx.app.debug(TAG, "Server: Sending message to the player " + player.getNumber() + ": " + message);
            }

            socket.getOutputStream().write(message.getBytes());
            socket.getOutputStream().flush();
        } catch (Exception e) {
            if (socketType == SocketType.SERVER_SOCKET) {
                Gdx.app.debug(TAG, "WARNING: Unable to send message to server: " + e.getMessage());
            } else if (socketType == SocketType.PLAYER_SOCKET) {
                Gdx.app.debug(TAG, "WARNING: Unable to send message to player: " + e.getMessage());
            }
        }
    }


    public void die() {
        running = false;

        if (socket != null) {
            socket.dispose();
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public Player getPlayerInfo() {
        return player;
    }

    public SocketType getSocketType() {
        return socketType;
    }
}
