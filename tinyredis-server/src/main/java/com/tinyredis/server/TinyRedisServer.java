package com.tinyredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyredis.core.KeyValueStore;
import com.tinyredis.core.TinyRedisEngine;
import com.tinyredis.protocol.CommandParser;
import com.tinyredis.protocol.ResponseEncoder;

public class TinyRedisServer {
    private final ServerSocket serverSocket;
    private volatile boolean running = true;

    private final KeyValueStore keyValueStore;
    private final TinyRedisEngine tinyRedisEngine;
    private final ResponseEncoder responseEncoder;
    private final CommandParser commandParser;
    private final CommandMapper commandMapper;
    private final ResponseMapper responseMapper;

    private static final Logger logger = LoggerFactory.getLogger(TinyRedisServer.class);

    public TinyRedisServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.keyValueStore = new KeyValueStore();
        this.tinyRedisEngine = new TinyRedisEngine(keyValueStore);
        this.responseEncoder = new ResponseEncoder();
        this.commandParser = new CommandParser();
        this.commandMapper = new CommandMapper();
        this.responseMapper = new ResponseMapper();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void start() throws IOException {
        while (running && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();

                logger.info("Client connected: {}", socket.getRemoteSocketAddress());

                ClientConnection connection = new ClientConnection(socket);
                ConnectionHandler handler = new ConnectionHandler(connection, commandParser, tinyRedisEngine,
                        commandMapper, responseMapper, responseEncoder);

                Thread thread = new Thread(() -> handler.handle());

                thread.start();
            } catch (SocketException e) {
                if (!running || serverSocket.isClosed()) {
                    logger.info("Server socket closed, stopping server loop.");
                    break;
                }
                throw e;
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
