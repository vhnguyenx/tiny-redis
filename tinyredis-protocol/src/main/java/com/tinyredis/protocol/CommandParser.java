package com.tinyredis.protocol;

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
        String header = RespInputReader.readLine(input);

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
        String header = RespInputReader.readLine(input);

        if (header.charAt(0) != '$') {
            throw new IOException("Invalid header");
        }

        int length = Integer.parseInt(header.substring(1));

        byte[] buffer = RespInputReader.readBytes(input, length);

        RespInputReader.readCRLF(input);

        String value = new String(buffer, StandardCharsets.UTF_8);

        return value;
    }

}
