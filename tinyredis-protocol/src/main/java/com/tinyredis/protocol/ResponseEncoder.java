package com.tinyredis.protocol;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder {

    public byte[] encode(RespValue response) {
        return switch (response.getType()) {
            case SIMPLE_STRING -> encodeSimpleString((String) response.getData());
            case ERROR -> encodeError((String) response.getData());
            case INTEGER -> encodeInteger(((Number) response.getData()).longValue());
            case BULK_STRING -> encodeBulkString((String) response.getData());
            case NULL -> encodeNull();
            case ARRAY -> throw new UnsupportedOperationException("ARRAY encoding is not implemented");
        };
    }

    public byte[] encodeSimpleString(String text) {
        return utf8("+" + text + "\r\n");
    }

    public byte[] encodeBulkString(String text) {
        if (text == null) {
            return encodeNull();
        }

        byte[] payload = utf8(text);
        byte[] header = utf8("$" + payload.length + "\r\n");
        byte[] suffix = utf8("\r\n");
        byte[] response = new byte[header.length + payload.length + suffix.length];

        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(payload, 0, response, header.length, payload.length);
        System.arraycopy(suffix, 0, response, header.length + payload.length, suffix.length);

        return response;
    }

    public byte[] encodeInteger(long number) {
        return utf8(":" + number + "\r\n");
    }

    public byte[] encodeNull() {
        return utf8("$-1\r\n");
    }

    public byte[] encodeError(String message) {
        return utf8("-ERR " + message + "\r\n");
    }

    private byte[] utf8(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
