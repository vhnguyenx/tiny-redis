package com.tinyredis.protocol;

import java.nio.charset.StandardCharsets;

public final class CommandEncoder {
    public byte[] encode(String... arguments) {
        StringBuilder request = new StringBuilder();
        request.append('*').append(arguments.length).append("\r\n");

        for (String argument : arguments) {
            byte[] payload = argument.getBytes(StandardCharsets.UTF_8);
            request.append('$').append(payload.length).append("\r\n");
            request.append(argument).append("\r\n");
        }

        return request.toString().getBytes(StandardCharsets.UTF_8);
    }
}
