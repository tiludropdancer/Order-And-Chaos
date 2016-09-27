package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Abstract base class for implementations of {@link GameConnector}.
 *
 * @author Anastasia Radchenko
 */
public abstract class AbstractGameConnector implements GameConnector {
    protected static final int GAME_PORT = 7777;

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    @Override
    public void connect(String hostNameOrIPAddress) throws IOException {
        try {
            socket = connectSocket(hostNameOrIPAddress);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    protected abstract Socket connectSocket(String hostNameOrIPAddress) throws IOException;

    @Override
    public void disconnect() {
        if (socket != null) {
            try { socket.close(); } catch (IOException e) {} finally { socket = null; }
        }
    }

    @Override
    public int readInt() throws IOException {
        return in.readInt();
    }

    @Override
    public String readString() throws IOException {
        return in.readUTF();
    }

    @Override
    public void writeInt(int value) throws IOException {
        out.writeInt(value);
    }

    @Override
    public void writeString(String value) throws IOException {
        out.writeUTF(value);
    }
}
