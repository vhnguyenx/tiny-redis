package com.tinyredis.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ResponseDecoder {
    public RespValue decode(InputStream input) throws IOException {
        String header = RespInputReader.readLine(input);

        return switch (header.charAt(0)) {
            case '+' -> new RespValue(ResponseType.SIMPLE_STRING, header.substring(1));
            case '-' -> new RespValue(ResponseType.ERROR, header.substring(1));
            case ':' -> new RespValue(ResponseType.INTEGER, Long.valueOf(header.substring(1)));
            case '$' -> decodeBulkString(input, Integer.parseInt(header.substring(1)));
            default -> throw new IOException("Unsupported RESP response");
        };
    }

    private RespValue decodeBulkString(InputStream input, int length) throws IOException {
        if (length == -1) {
            return new RespValue(ResponseType.NULL, null);
        }

        byte[] payload = RespInputReader.readBytes(input, length);

        RespInputReader.readCRLF(input);
        return new RespValue(ResponseType.BULK_STRING, new String(payload, StandardCharsets.UTF_8));
    }
}
