package com.tinyredis.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    void parseSetCommandWithArguments() throws IOException {
        Command command = parser.parse(input(respArray("SET", "key", "value")));

        assertEquals(CommandType.SET, command.getType());
        assertEquals(List.of("key", "value"), command.getArguments());
    }

    @Test
    void parseCommandTypeCaseInsensitively() throws IOException {
        Command command = parser.parse(input(respArray("get", "key")));

        assertEquals(CommandType.GET, command.getType());
        assertEquals(List.of("key"), command.getArguments());
    }

    @Test
    void parseUtf8BulkStringUsingByteLength() throws IOException {
        Command command = parser.parse(input(respArray("SET", "name", "caf\u00e9")));

        assertEquals(CommandType.SET, command.getType());
        assertEquals(List.of("name", "caf\u00e9"), command.getArguments());
    }

    @Test
    void parseBulkStringContainingCrLf() throws IOException {
        Command command = parser.parse(input(respArray("SET", "notes", "line\r\nbreak")));

        assertEquals(CommandType.SET, command.getType());
        assertEquals(List.of("notes", "line\r\nbreak"), command.getArguments());
    }

    @Test
    void throwIOExceptionWhenArrayHeaderIsInvalid() {
        String request = "2\r\n$3\r\nGET\r\n$3\r\nkey\r\n";

        assertThrows(IOException.class, () -> parser.parse(input(request)));
    }

    @Test
    void throwIOExceptionWhenBulkStringHeaderIsInvalid() {
        String request = "*1\r\n3\r\nGET\r\n";

        assertThrows(IOException.class, () -> parser.parse(input(request)));
    }

    @Test
    void throwIOExceptionWhenBulkStringTerminatorIsInvalid() {
        String request = "*1\r\n$3\r\nGET\rX";

        assertThrows(IOException.class, () -> parser.parse(input(request)));
    }

    @Test
    void throwIOExceptionWhenStreamEndsBeforeLineCompletes() {
        String request = "*1\r\n$3";

        assertThrows(IOException.class, () -> parser.parse(input(request)));
    }

    @Test
    void throwIOExceptionWhenStreamEndsBeforeBulkStringCompletes() {
        String request = "*2\r\n$3\r\nGET\r\n$5\r\nkey";

        assertThrows(IOException.class, () -> parser.parse(input(request)));
    }

    @Test
    void throwIllegalArgumentExceptionWhenCommandTypeIsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input(respArray("PING"))));
    }

    private static InputStream input(String request) {
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
    }

    private static String respArray(String... values) {
        StringBuilder builder = new StringBuilder("*")
                .append(values.length)
                .append("\r\n");

        for (String value : values) {
            builder.append("$")
                    .append(value.getBytes(StandardCharsets.UTF_8).length)
                    .append("\r\n")
                    .append(value)
                    .append("\r\n");
        }

        return builder.toString();
    }
}
