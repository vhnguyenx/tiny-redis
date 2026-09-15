package com.tinyredis.protocol;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.Value;
import com.tinyredis.core.ValueType;

class ResponseEncoderTest {

    private final ResponseEncoder encoder = new ResponseEncoder();

    @Test
    void encodeSuccessReturnsOkSimpleString() {
        byte[] actual = encoder.encode(EngineResult.success());

        assertResponse("+OK\r\n", actual);
    }

    @Test
    void encodeValueReturnsBulkString() {
        Value value = new Value(ValueType.STRING, "hello");

        byte[] actual = encoder.encode(EngineResult.value(value));

        assertResponse("$5\r\nhello\r\n", actual);
    }

    @Test
    void encodeValueWithUnicodeUsesUtf8ByteLength() {
        Value value = new Value(ValueType.STRING, "你好");

        byte[] actual = encoder.encode(EngineResult.value(value));

        assertResponse("$6\r\n你好\r\n", actual);
    }

    @Test
    void encodeValueWithNullReturnsNullBulkString() {
        Value value = new Value(ValueType.STRING, null);

        byte[] actual = encoder.encode(EngineResult.value(value));

        assertResponse("$-1\r\n", actual);
    }

    @Test
    void encodeIntegerReturnsIntegerResponse() {
        byte[] actual = encoder.encode(EngineResult.integer(1));

        assertResponse(":1\r\n", actual);
    }

    @Test
    void encodeZeroReturnsZeroIntegerResponse() {
        byte[] actual = encoder.encode(EngineResult.integer(0));

        assertResponse(":0\r\n", actual);
    }

    @Test
    void encodeMissingReturnsNullBulkString() {
        byte[] actual = encoder.encode(EngineResult.missing());

        assertResponse("$-1\r\n", actual);
    }

    @Test
    void encodeErrorReturnsErrorResponse() {
        byte[] actual = encoder.encode(
                EngineResult.error("wrong number of arguments"));

        assertResponse("-ERR wrong number of arguments\r\n", actual);
    }

    private static void assertResponse(String expected, byte[] actual) {
        assertArrayEquals(
                expected.getBytes(StandardCharsets.UTF_8),
                actual);
    }
}
