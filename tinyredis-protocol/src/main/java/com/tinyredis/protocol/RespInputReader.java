package com.tinyredis.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class RespInputReader {
    private RespInputReader() {
    }

    static String readLine(InputStream input) throws IOException {
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

    static byte[] readBytes(InputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        int offset = 0;

        while (offset < length) {
            int current = input.read(buffer, offset, length - offset);

            if (current == -1) {
                throw new IOException("Unexpected end of stream");
            }

            offset += current;
        }

        return buffer;
    }

    static void readCRLF(InputStream input) throws IOException {
        int first = input.read();
        int second = input.read();

        if (first == -1 || second == -1) {
            throw new IOException("Unexpected end of stream");
        }

        if (first != '\r' || second != '\n') {
            throw new IOException("Invalid CRLF");
        }
    }
}
