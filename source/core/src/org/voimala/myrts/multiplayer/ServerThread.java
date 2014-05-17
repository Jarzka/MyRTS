package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private ServerSocketHints serverSocketHint;
    private ServerSocket serverSocket;
    private boolean running = true;
    private int port = 0;
    private Socket socket;
    private ArrayList<ConnectedClientThread> connectedClients = new ArrayList<ConnectedClientThread>();

    public ServerThread(final int port) {
        super(ServerThread.class.getName());

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        serverSocketHint = new ServerSocketHints();
        serverSocketHint.acceptTimeout = 100000;
        this.port = port;
    }

    public void run() {
        System.out.println("Creating server"); // TODO Testing
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, serverSocketHint);
        System.out.println("Server created"); // TODO Testing

        // Wait for clients to connect
        while (running) {
            System.out.println("Listening connections...");

            socket = serverSocket.accept(null);

            System.out.println("Client connected"); // TODO Testing

            ConnectedClientThread client = new ConnectedClientThread(socket);
            connectedClients.add(client);
            client.start();
        }
    }

    public void sendMessageToClients(final String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            // Continue
        }
    }

    public void die() {
        for (ConnectedClientThread client : connectedClients) {
            client.die();
        }

        running = false;
    }
}
