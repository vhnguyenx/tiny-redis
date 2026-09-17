package com.tinyredis.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.TinyRedisEngine;
import com.tinyredis.protocol.Command;
import com.tinyredis.protocol.CommandParser;
import com.tinyredis.protocol.RespValue;
import com.tinyredis.protocol.ResponseEncoder;

public class ConnectionHandler {
    private final ClientConnection connection;
    private final CommandParser commandParser;
    private final TinyRedisEngine engine;
    private final CommandMapper commandMapper;
    private final ResponseMapper responseMapper;
    private final ResponseEncoder responseEncoder;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public ConnectionHandler(ClientConnection connection, CommandParser commandParser, TinyRedisEngine engine,
            CommandMapper commandMapper, ResponseMapper responseMapper, ResponseEncoder responseEncoder) {
        this.connection = connection;
        this.commandParser = commandParser;
        this.engine = engine;
        this.commandMapper = commandMapper;
        this.responseMapper = responseMapper;
        this.responseEncoder = responseEncoder;
    }

    public void handle() {
        try {
            while (connection.isOpen()) {
                Command command = commandParser.parse(connection.getInputStream());

                logger.info("Received command: {}", command.getType());

                EngineResult result = engine.execute(commandMapper.map(command));
                RespValue responseValue = responseMapper.map(result);

                byte[] response = responseEncoder.encode(responseValue);

                connection.write(response);
            }
        } catch (IOException e) {
            if ("Unexpected end of stream".equals(e.getMessage())) {
                logger.info("Client disconnected cleanly");
            } else {
                logger.error("Client connection error", e);
            }
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Failed to close client connection", e);
            }
        }
    }
}
