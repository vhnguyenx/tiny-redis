package com.tinyredis.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    public Command parse(InputStream input) throws IOException {
        List<String> commandList = parseArray(input);
       
        CommandType commandType = CommandType.valueOf(commandList.get(0).toUpperCase());

        List<String> arguList = new ArrayList<>();

        for (int i = 1; i < commandList.size(); i++) {
            arguList.add(commandList.get(i));
        }

        Command command = new Command(commandType, arguList);

        return command;
    }

    private List<String> parseArray(InputStream input) throws IOException {
        String header = readLine(input);

        if (header.charAt(0) != '*') {
            throw new IOException("Invalid header");
        }

        List<String> command = new ArrayList<>();

        int count = Integer.parseInt(header.substring(1));

        for (int i = 0; i < count; i++) {
            command.add(parseBulkString(input));
        }

        return command;
    }

    private String parseBulkString(InputStream input) throws IOException {
        String header = readLine(input);

        if (header.charAt(0) != '$') {
            throw new IOException("Invalid header");
        }

        int length = Integer.parseInt(header.substring(1));

        byte[] buffer = readBytes(input, length);

        readCRLF(input);

        String value = new String(buffer, StandardCharsets.UTF_8);

        return value;
    }

    private String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while (true) {
            int current = input.read();

            if (current == -1) {
                throw new IOException("Unexpected end of stream");
            }

            if (current == '\r') {
                int next = input.read();

                if (next == -1) {
                    throw new IOException("Unexpected end of stream");
                }

                if (next != '\n') {
                    throw new IOException("Invalid CRLF");
                }

                return buffer.toString(StandardCharsets.UTF_8);
            }

            buffer.write(current);
        }
    }

    private byte[] readBytes(InputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];

        int offset = 0;

        while (offset < length) {
            int remaining = length - offset;
            int current = input.read(buffer, offset, remaining);

            if (current == -1) {
                throw new IOException("Unexpected end of stream");
            }

            offset += current;
        }

        return buffer;
    }

    private void readCRLF(InputStream input) throws IOException {
        int first = input.read();
        int second = input.read();

        if (first != '\r' || second != '\n') {
            throw new IOException("Invalid CRLF");
        }
    }
}
