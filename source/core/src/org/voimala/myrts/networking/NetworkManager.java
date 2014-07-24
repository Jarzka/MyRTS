package org.voimala.myrts.networking;

/** The main purpose of this class is to host a new game (start server thread)
 * and join to a game (start listen socket thread). */
public class NetworkManager {

    private ServerThread serverThread;
    private ListenSocketThread listenSocketThread;

    public final int DEFAULT_PORT = 52829;
    public final long NETWORK_MESSAGE_MAX_LENGTH_CHARACTERS = 262144;
    public final int SLOTS_MAX = 30;

    private boolean isHost = false;
    private int joinPort;
    private String joinIp;

    private static NetworkManager instanceOfThis;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new NetworkManager();
        }

        return instanceOfThis;
    }

    public void joinGame() {
        if (listenSocketThread == null) {
            listenSocketThread = new ListenSocketThread(joinIp, joinPort);
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
    public ListenSocketThread getClientThread() {
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

        ChatContainer.getInstance().clearAllChatMessages();
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

    public void setJoinPort(final int port) {
        this.joinPort = port;
    }

    public void setJoinIp(final String ip) {
        this.joinIp = ip;
    }

}
