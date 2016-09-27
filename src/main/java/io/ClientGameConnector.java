package io;

import java.io.IOException;
import java.net.Socket;

/**
 * Implements client version of {@link GameConnector}.
 *
 * @author Anastasia Radchenko
 */
public class ClientGameConnector extends AbstractGameConnector {
    @Override
    protected Socket connectSocket(String hostNameOrIPAddress) throws IOException {
        return new Socket(hostNameOrIPAddress, GAME_PORT);
    }
}
