package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import org.voimala.myrts.app.MyRTS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private static final String TAG = ServerThread.class.getName();
    private ServerSocketHints serverSocketHint;
    private ServerSocket serverSocket;
    private boolean running = true;
    private int port = 0;
    private Socket socket;
    private ArrayList<ClientThread> connectedClients = new ArrayList<ClientThread>();

    public ServerThread(final int port) {
        super(ServerThread.class.getName());

        Gdx.app.setLogLevel(MyRTS.LOG_LEVEL);

        serverSocketHint = new ServerSocketHints();
        serverSocketHint.acceptTimeout = 100000;
        this.port = port;
    }

    public void run() {
        Gdx.app.debug(TAG, "Creating server...");
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, serverSocketHint);
        Gdx.app.debug(TAG, "Server created");

        // Wait for clients to connect
        while (running) {
            Gdx.app.debug(TAG, "Listening connections...");

            try {
                socket = serverSocket.accept(null);

                Gdx.app.debug(TAG, "Client connected from" + " " + socket.getRemoteAddress());

                ClientThread client = new ClientThread(socket);
                connectedClients.add(client);
                client.start();

                sendMessageOfTheDay(client);
            } catch (Exception e) {
                Gdx.app.debug(TAG, "Error accepting client connection: " + e.getMessage());
            }
        }

        Gdx.app.debug(TAG, "Server stopped.");
    }

    private void sendMessageOfTheDay(ClientThread client) {
        String motd = "<MOTD|Welcome to the server.>"; // TODO Hardcoded.
        Gdx.app.debug(TAG, "Sending message of the day to the client: " + motd);
        try {
            client.getSocket().getOutputStream().write(motd.getBytes());
        } catch (Exception e) {
            Gdx.app.debug(TAG, "WARNING: Unable to send message to client." + " " + e.getMessage());
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
