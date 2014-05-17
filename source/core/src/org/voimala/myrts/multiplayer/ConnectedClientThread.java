package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.net.Socket;
import org.voimala.myrts.scenes.gameplay.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** This class listens messages from the connected client. */

// TODO Merge with ClientThread?
public class ConnectedClientThread extends Thread {
    private Socket socket;
    private boolean running = true;
    private Player player;

    public ConnectedClientThread(final Socket socket) {
        super(ConnectedClientThread.class.getName());
        this.socket = socket;

        player = new Player();
    }

    public void run() {
        while (running) {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Got a message from the client."); // TODO Testing

            try {
                System.out.println(buffer.readLine()); // TODO TEsting
            } catch (IOException e) {
                // Continue
            }
        }
    }

    public void die() {
        running = false;
    }
}
