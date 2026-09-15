package com.tinyredis.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.TinyRedisEngine;
import com.tinyredis.protocol.Command;
import com.tinyredis.protocol.CommandParser;
import com.tinyredis.protocol.ResponseEncoder;

public class ConnectionHandler {
    private final ClientConnection connection;
    private final CommandParser commandParser;
    private final TinyRedisEngine engine;
    private final ResponseEncoder responseEncoder;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public ConnectionHandler(ClientConnection connection, CommandParser commandParse, TinyRedisEngine engine, ResponseEncoder responseEncoder) {
        this.connection = connection;
        this.commandParser = commandParse;
        this.engine = engine;
        this.responseEncoder = responseEncoder;
    }

    public void handle() {
        try {
            while (connection.isOpen()) {
                Command command = commandParser.parse(connection.getInputStream());

                logger.info("Receive command :" + command.getType());

                EngineResult result = engine.execute(command);

                byte[] response = responseEncoder.encode(result);

                connection.write(response);
            }
        } catch (IOException e) {
            logger.error("Client connection closed or I/O failed", e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Failed to close client connection", e);
            }
        }
    }
}
