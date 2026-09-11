package com.tinyredis.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler {
    private final ClientConnection connection;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public ConnectionHandler(ClientConnection connection) {
        this.connection = connection;
    }

    public void handle() {
        try {
            while (connection.isOpen()) {
                byte[] data = connection.read();

                if (data == null) {
                    connection.close();
                    break;
                }

                logger.info("Received: {}", new String(data));

                connection.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
