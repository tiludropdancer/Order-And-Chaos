package io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implements client version of {@link GameConnector}.
 *
 * @author Anastasia Radchenko
 */
public class ServerGameConnector extends AbstractGameConnector {
    @Override
    protected Socket connectSocket(String hostNameOrIPAddress) throws IOException {
        ServerSocket serverSocket = new ServerSocket(GAME_PORT);
        return serverSocket.accept();
    }
}
