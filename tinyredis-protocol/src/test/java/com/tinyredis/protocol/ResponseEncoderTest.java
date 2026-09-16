package com.tinyredis.protocol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ResponseEncoderTest {
    private final ResponseEncoder encoder = new ResponseEncoder();

    @Test
    void encodeSimpleString() {
        assertResponse("+OK\r\n", encoder.encode(new RespValue(ResponseType.SIMPLE_STRING, "OK")));
    }

    @Test
    void encodeBulkString() {
        assertResponse("$5\r\nhello\r\n", encoder.encode(new RespValue(ResponseType.BULK_STRING, "hello")));
    }

    @Test
    void encodeUnicodeBulkStringUsesUtf8ByteLength() {
        assertResponse("$6\r\n你好\r\n", encoder.encode(new RespValue(ResponseType.BULK_STRING, "你好")));
    }

    @Test
    void encodeInteger() {
        assertResponse(":1\r\n", encoder.encode(new RespValue(ResponseType.INTEGER, 1L)));
    }

    @Test
    void encodeNull() {
        assertResponse("$-1\r\n", encoder.encode(new RespValue(ResponseType.NULL, null)));
    }

    @Test
    void encodeError() {
        assertResponse("-ERR invalid\r\n", encoder.encode(new RespValue(ResponseType.ERROR, "invalid")));
    }

    private static void assertResponse(String expected, byte[] actual) {
        assertArrayEquals(expected.getBytes(StandardCharsets.UTF_8), actual);
    }
}
