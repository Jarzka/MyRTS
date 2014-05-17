package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.voimala.myrts.scenes.gameplay.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientThread extends Thread {

    private static final String TAG = ClientThread.class.getName();
    private SocketHints socketHints;
    private int port = 0;
    private String ip;
    private Socket socket;
    private boolean running = true;
    private Player player;

    /** Used when the server creates a new thread for the connected client. */
    public ClientThread(final Socket socket) {
        super(ClientThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;
        this.socket = socket;
        player = new Player();
    }

    /** Used for connecting to the server. */
    public ClientThread(final String ip, final int port) {
        super(ClientThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;
        this.ip = ip;
        this.port = port;
        player = new Player();
    }

    public void run() {
        connectToTheServer();

        while (running) {
            Gdx.app.debug(TAG, "Listening messages from the server.");

            BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            try {
                Gdx.app.debug(TAG, "Got message from the server: " + buffer.readLine());
            } catch (IOException e) {
                Gdx.app.debug(TAG, "ERROR: while reading buffer: " + e.getMessage());
            }
        }
    }

    private void connectToTheServer() {
        if (socket == null) {
            Gdx.app.debug(TAG, "Connecting to the server...");
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, ip, port, socketHints);

            Gdx.app.debug(TAG, "Connected.");
        }
    }

    public void sendMessage(final String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            Gdx.app.debug(TAG, "WARNING: Unable to send message to client." + " " + e.getMessage());
        }
    }

    public void die() {
        running = false;
    }

    public Socket getSocket() {
        return socket;
    }
}
