package com.tinyredis.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    public byte[] read() throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead = input.read(buffer);

        if (bytesRead == -1) {
            return null;
        }

        return Arrays.copyOf(buffer, bytesRead);
    }

    public InputStream getInputStream() {
        return input;
    }

    public void write(byte[] data) throws IOException {
        output.write(data);
    }

    public boolean isOpen() {
        return !socket.isClosed();
    }

    public void close() throws IOException {
        socket.close();
    }
}
