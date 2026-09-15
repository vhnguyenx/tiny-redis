package com.tinyredis.protocol;

import java.nio.charset.StandardCharsets;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.ResultType;
import com.tinyredis.core.Value;

public class ResponseEncoder {

    public byte[] encode(EngineResult result) {
        ResultType type = result.getType();
        Object data = result.getData();

        return switch (type) {
            case SUCCESS -> encodeSimpleString("OK");
            case VALUE -> {
                Value value = (Value) data;
                Object rawData = value.getData();

                if (rawData == null) {
                    yield encodeNull();
                }

                yield encodeBulkString(rawData.toString());
            }
            case INTEGER -> {
                long number = ((Number) data).longValue();
                yield encodeInteger(number);
            }
            case MISSING -> encodeNull();
            case ERROR -> encodeError((String) data);
        };
    }

    public byte[] encodeSimpleString(String text) {
        String response = "+" + text + "\r\n";
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] encodeBulkString(String text) {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        byte[] header = ("$" + payload.length + "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] suffix = "\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] response = new byte[header.length + payload.length + suffix.length];

        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(payload, 0, response, header.length, payload.length);
        System.arraycopy(suffix, 0, response, header.length + payload.length, suffix.length);

        return response;
    }

    public byte[] encodeInteger(long number) {
        String response = ":" + number + "\r\n";
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] encodeNull() {
        String response = "$-1\r\n";
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] encodeError(String message) {
        String response = "-ERR " + message + "\r\n";
        return response.getBytes(StandardCharsets.UTF_8);
    }
}
