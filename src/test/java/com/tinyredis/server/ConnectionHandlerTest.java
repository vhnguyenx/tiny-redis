package com.tinyredis.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.KeyValueStore;
import com.tinyredis.core.TinyRedisEngine;
import com.tinyredis.core.ValueType;
import com.tinyredis.protocol.Command;
import com.tinyredis.protocol.CommandParser;
import com.tinyredis.protocol.CommandType;
import com.tinyredis.protocol.ResponseEncoder;

class ConnectionHandlerTest {

    @Test
    void handleExecutesCommandEncodesResultAndWritesResponse() throws IOException {
        ClientConnection connection = org.mockito.Mockito.mock(ClientConnection.class);
        CommandParser commandParser = org.mockito.Mockito.mock(CommandParser.class);
        TinyRedisEngine engine = org.mockito.Mockito.mock(TinyRedisEngine.class);
        ResponseEncoder responseEncoder = org.mockito.Mockito.mock(ResponseEncoder.class);

        InputStream input = new ByteArrayInputStream(new byte[0]);
        Command command = new Command(CommandType.GET, List.of("name"));
        EngineResult result = EngineResult.value(null);
        byte[] response = "$-1\r\n".getBytes();

        when(connection.isOpen()).thenReturn(true, false);
        when(connection.getInputStream()).thenReturn(input);
        when(commandParser.parse(input)).thenReturn(command);
        when(engine.execute(command)).thenReturn(result);
        when(responseEncoder.encode(result)).thenReturn(response);

        ConnectionHandler handler = new ConnectionHandler(
                connection,
                commandParser,
                engine,
                responseEncoder);

        handler.handle();

        verify(commandParser).parse(input);
        verify(engine).execute(command);
        verify(responseEncoder).encode(result);
        verify(connection).write(response);
        verify(connection).close();
    }

    @Test
    void handleClosesConnectionWhenParsingFails() throws IOException {
        ClientConnection connection = org.mockito.Mockito.mock(ClientConnection.class);
        CommandParser commandParser = org.mockito.Mockito.mock(CommandParser.class);
        TinyRedisEngine engine = org.mockito.Mockito.mock(TinyRedisEngine.class);
        ResponseEncoder responseEncoder = org.mockito.Mockito.mock(ResponseEncoder.class);

        InputStream input = new ByteArrayInputStream(new byte[0]);
        IOException parsingFailure = new IOException("invalid request");

        when(connection.isOpen()).thenReturn(true);
        when(connection.getInputStream()).thenReturn(input);
        when(commandParser.parse(input)).thenThrow(parsingFailure);

        ConnectionHandler handler = new ConnectionHandler(
                connection,
                commandParser,
                engine,
                responseEncoder);

        handler.handle();

        verify(connection).close();
        verify(engine, never()).execute(org.mockito.ArgumentMatchers.any());
        verify(responseEncoder, never()).encode(org.mockito.ArgumentMatchers.any());
        verify(connection, never()).write(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void handleStoresValueAndReturnsItForTheNextCommand() throws IOException {
        ClientConnection connection = org.mockito.Mockito.mock(ClientConnection.class);
        CommandParser commandParser = org.mockito.Mockito.mock(CommandParser.class);
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        ResponseEncoder responseEncoder = new ResponseEncoder();

        InputStream input = new ByteArrayInputStream(new byte[0]);
        Command setCommand = new Command(CommandType.SET, List.of("name", "tinyredis"));
        Command getCommand = new Command(CommandType.GET, List.of("name"));

        when(connection.isOpen()).thenReturn(true, true, false);
        when(connection.getInputStream()).thenReturn(input);
        when(commandParser.parse(input)).thenReturn(setCommand, getCommand);

        ConnectionHandler handler = new ConnectionHandler(
                connection,
                commandParser,
                engine,
                responseEncoder);

        handler.handle();

        assertEquals(ValueType.STRING, store.get("name").getType());
        assertEquals("tinyredis", store.get("name").getData());

        ArgumentCaptor<byte[]> responseCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(connection, org.mockito.Mockito.times(2)).write(responseCaptor.capture());

        List<byte[]> responses = responseCaptor.getAllValues();
        assertArrayEquals(
                "+OK\r\n".getBytes(StandardCharsets.UTF_8),
                responses.get(0));
        assertArrayEquals(
                "$9\r\ntinyredis\r\n".getBytes(StandardCharsets.UTF_8),
                responses.get(1));
    }
}