package org.voimala.myrts.networking;

public class NetworkManager {

    private ServerThread serverThread;
    private ListenSocketThread listenSocketThread;

    public final int DEFAULT_PORT = 52829;

    private boolean isHost = false;

    private static NetworkManager instanceOfThis;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new NetworkManager();
        }

        return instanceOfThis;
    }

    public void joinGame(final String ip, final int port) {
        if (listenSocketThread == null) {
            listenSocketThread = new ListenSocketThread(ip, port);
            listenSocketThread.start();
        }
    }

    /** Hosts a new game if it is not already hosted by creating a new server thread. */
    public void hostGame(final int port) {
        if (serverThread == null) {
            serverThread = new ServerThread(port);
            serverThread.start();
        }
    }

    /** @return Returns null if thread is not in use. */
    public ServerThread getServerThread() {
        return serverThread;
    }

    /** @return Returns null if thread is not in use. */
    public ListenSocketThread getListenSocketThread() {
        return listenSocketThread;
    }

    /** Stops the server and disconnects the client */
    public void disconnectAll() {
        disconnectClientThread();
        disconnectServerThread();
    }

    public void disconnectClientThread() {
        if (listenSocketThread != null) {
            listenSocketThread.die();
            listenSocketThread = null;
        }

        Chat.getInstance().clearAllChatMessages();
    }

    public void disconnectServerThread() {
        if (serverThread != null) {
            serverThread.die();
            serverThread = null;
        }

        isHost = false;
    }

    public boolean isHost() {
        return isHost;
    }

    /** Warning: only server thread should call this method! */
    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public ConnectionState getClientConnectionState() {
        if (listenSocketThread == null) {
            return ConnectionState.NOT_CONNECTED;
        }

        return listenSocketThread.getConnectionState();
    }

}
