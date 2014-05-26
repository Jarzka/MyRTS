package org.voimala.myrts.multiplayer;

import org.voimala.myrts.app.CommandLineParser;

public class NetworkManager {

    private ServerThread serverThread;
    private ClientThread clientThread;

    private boolean isHost = false;
    private boolean isClientSocketConnected = false;

    private static NetworkManager instanceOfThis;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new NetworkManager();
        }

        return instanceOfThis;
    }

    public void joinGame(final String ip, final int port) {
        clientThread = new ClientThread(ip, port);
        clientThread.start();
    }

    /** Hosts a new game if not already hosted. */
    public void hostGame(final int port) {
        if (serverThread == null) {
            isHost = true;
            serverThread = new ServerThread(port);
            serverThread.start();
        }

    }

    /** @return Returns null if thread is not in use. */
    public ServerThread getServerThread() {
        return serverThread;
    }

    /** @return Returns null if thread is not in use. */
    public ClientThread getClientThread() {
        return clientThread;
    }

    public void quit() {
        if (clientThread != null) {
            clientThread.die();
        }

        if (serverThread != null) {
            serverThread.die();
        }

        isHost = false;
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isClientSocketConnected() {
        if (clientThread == null) {
            return false;
        }

        return clientThread.getSocket().isConnected();
    }

}
