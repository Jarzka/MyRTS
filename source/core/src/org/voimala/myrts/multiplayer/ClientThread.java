package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.voimala.myrts.scenes.gameplay.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/** This class is used for connecting to the server and listening server messages. */

public class ClientThread extends Thread {
    private SocketHints socketHints;
    private int port = 0;
    private String ip;
    private Socket socket;
    private boolean running = true;
    private Player player;

    public ClientThread(final String ip, final int port) {
        super(ClientThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;

        this.ip = ip;
        this.port = port;

        player = new Player();
    }

    public void run() {
        System.out.println("Connecting to the server..."); // TODO Testing
        socket = Gdx.net.newClientSocket(Net.Protocol.TCP, ip, port, socketHints);

        System.out.println("Connected."); // TODO Testing

        while (running) {
            System.out.println("Listening messages from the server."); // TODO Testing

            BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Got message from the server."); // TODO Testing

            try {
                System.out.println(buffer.readLine()); // TODO Testing
            } catch (IOException e) {
                // Continue
            }
        }
    }

    public void sendMessage(final String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            // Continue
        }
    }

    public void die() {
        running = false;
    }
}
