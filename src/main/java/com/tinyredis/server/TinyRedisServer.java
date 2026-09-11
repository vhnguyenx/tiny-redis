package com.tinyredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyRedisServer {
    private final ServerSocket serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(TinyRedisServer.class);

    public TinyRedisServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();

            logger.info("Client connected: {}", socket.getRemoteSocketAddress());

            ClientConnection connection = new ClientConnection(socket);

            ConnectionHandler handler = new ConnectionHandler(connection);

            Thread thread = new Thread(() -> handler.handle());

            thread.start();
        }
    }
}
