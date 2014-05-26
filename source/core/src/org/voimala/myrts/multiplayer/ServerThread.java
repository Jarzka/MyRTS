package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.voimala.myrts.app.GameMain;

import java.io.IOException;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private static final String TAG = ServerThread.class.getName();
    private ServerSocketHints serverSocketHints;
    private ServerSocket serverSocket;
    private boolean running = true;
    private int port = 0;
    private Socket socket;
    private ArrayList<ClientThread> connectedClients = new ArrayList<ClientThread>();

    public ServerThread(final int port) {
        super(ServerThread.class.getName());

        serverSocketHints = new ServerSocketHints();
        serverSocketHints.acceptTimeout = 100000;
        serverSocketHints.receiveBufferSize = 90000;
        this.port = port;
    }

    public void run() {
        createServer();
        acceptConnections();

        Gdx.app.debug(TAG, "Server stopped.");
    }

    private void createServer() {
        try {
            Gdx.app.debug(TAG, "Creating a server...");
            serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, serverSocketHints);
            Gdx.app.debug(TAG, "Server created");
        } catch (Exception e) {
            Gdx.app.debug(TAG, "Error creating a server: " + e.getMessage());
            running = false;
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
                socket = serverSocket.accept(socketHints);

                Gdx.app.debug(TAG, "Client connected from" + " " + socket.getRemoteAddress());

                ClientThread client = new ClientThread(socket);
                connectedClients.add(client);
                client.start();

                //RTSProtocolManager.getInstance().generateMessageOfTheDay(client); TODO
            } catch (Exception e) {
                Gdx.app.debug(TAG, "Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public void sendMessageToAllClients(final String message) {
        try {
            for (ClientThread client : connectedClients) {
                client.getSocket().getOutputStream().write(message.getBytes());
            }
        } catch (IOException e) {
            Gdx.app.debug(TAG, "WARNING: Unable to send message to client." + " " + e.getMessage());
        }
    }

    public void die() {
        for (ClientThread client : connectedClients) {
            client.die();
        }

        running = false;
    }
}
