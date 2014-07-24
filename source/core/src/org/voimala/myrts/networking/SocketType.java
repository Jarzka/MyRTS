package org.voimala.myrts.networking;

public enum SocketType {
    SERVER_SOCKET, /// The socket is connected to the server
    PLAYER_SOCKET, /// The socket is connected to a player (who is connected to the server)
}
