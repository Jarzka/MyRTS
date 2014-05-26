package org.voimala.myrts.multiplayer;

import org.voimala.myrts.app.CommandLineParser;

public class NetworkManager {

    private ServerThread serverThread;
    private ClientThread clientThread;

    private static NetworkManager instanceOfThis;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new NetworkManager();
        }

        return instanceOfThis;
    }

    public void joinGame() {
        clientThread = new ClientThread(
                CommandLineParser.getInstance().getCommandLineArguments().get("-ip"),
                Integer.valueOf(CommandLineParser.getInstance().getCommandLineArguments().get("-port")));
        clientThread.start();
    }

    public void hostGame() {
        serverThread = new ServerThread(
                Integer.valueOf(CommandLineParser.getInstance().getCommandLineArguments().get("-port")));
        serverThread.start();
    }

    public ServerThread getServerThread() {
        return serverThread;
    }

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
    }

}
