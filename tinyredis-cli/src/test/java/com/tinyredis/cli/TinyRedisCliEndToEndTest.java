package com.tinyredis.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tinyredis.server.TinyRedisServer;

class TinyRedisCliEndToEndTest {

    private TinyRedisServer server;
    private Thread serverThread;
    private int port;
    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws Exception {
        server = new TinyRedisServer(0);
        port = server.getPort();

        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                // Server closed
            }
        });
        serverThread.start();

        originalIn = System.in;
        originalOut = System.out;
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setIn(originalIn);
        System.setOut(originalOut);

        if (server != null) {
            server.stop();
        }
        if (serverThread != null) {
            serverThread.join(2000);
        }
    }

    @Test
    void testCliInteractiveCommands() throws Exception {
        String inputCommands = String.join("\n",
                "SET user:1 Alice",
                "GET user:1",
                "EXISTS user:1",
                "DEL user:1",
                "GET user:1",
                "quit",
                ""
        );

        ByteArrayInputStream inStream = new ByteArrayInputStream(inputCommands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outStream, true, StandardCharsets.UTF_8);

        System.setIn(inStream);
        System.setOut(printStream);

        TinyRedisCli.main(new String[]{"127.0.0.1", String.valueOf(port)});

        String cliOutput = outStream.toString(StandardCharsets.UTF_8);

        assertTrue(cliOutput.contains("tiny-redis:" + port + ">"));
        assertTrue(cliOutput.contains("OK"));
        assertTrue(cliOutput.contains("Alice"));
        assertTrue(cliOutput.contains("(nil)"));
    }
}
