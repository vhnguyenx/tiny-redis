package com.tinyredis.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tinyredis.protocol.CommandEncoder;
import com.tinyredis.protocol.ResponseDecoder;
import com.tinyredis.protocol.ResponseType;
import com.tinyredis.protocol.RespValue;

class TinyRedisEndToEndTest {

    private TinyRedisServer server;
    private Thread serverThread;
    private int port;
    private CommandEncoder encoder;
    private ResponseDecoder decoder;

    @BeforeEach
    void setUp() throws Exception {
        server = new TinyRedisServer(0); // dynamic free port
        port = server.getPort();

        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                // Server stopped
            }
        });
        serverThread.start();

        encoder = new CommandEncoder();
        decoder = new ResponseDecoder();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
        if (serverThread != null) {
            serverThread.join(2000);
        }
    }

    @Test
    void testBasicSetGetProtocolFlow() throws Exception {
        try (Socket socket = new Socket("127.0.0.1", port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            // SET key hello_world
            out.write(encoder.encode("SET", "greeting", "hello_world"));
            out.flush();
            RespValue setResp = decoder.decode(in);
            assertEquals(ResponseType.SIMPLE_STRING, setResp.getType());
            assertEquals("OK", setResp.getData());

            // GET greeting
            out.write(encoder.encode("GET", "greeting"));
            out.flush();
            RespValue getResp = decoder.decode(in);
            assertEquals(ResponseType.BULK_STRING, getResp.getType());
            assertEquals("hello_world", getResp.getData());
        }
    }

    @Test
    void testNonExistentKeyExistsAndDel() throws Exception {
        try (Socket socket = new Socket("127.0.0.1", port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            // GET non_existent
            out.write(encoder.encode("GET", "non_existent"));
            out.flush();
            RespValue getNilResp = decoder.decode(in);
            assertEquals(ResponseType.NULL, getNilResp.getType());

            // EXISTS non_existent
            out.write(encoder.encode("EXISTS", "non_existent"));
            out.flush();
            RespValue exists0Resp = decoder.decode(in);
            assertEquals(ResponseType.INTEGER, exists0Resp.getType());
            assertEquals(0L, exists0Resp.getData());

            // SET item sword
            out.write(encoder.encode("SET", "item", "sword"));
            out.flush();
            decoder.decode(in);

            // EXISTS item
            out.write(encoder.encode("EXISTS", "item"));
            out.flush();
            RespValue exists1Resp = decoder.decode(in);
            assertEquals(ResponseType.INTEGER, exists1Resp.getType());
            assertEquals(1L, exists1Resp.getData());

            // DEL item
            out.write(encoder.encode("DEL", "item"));
            out.flush();
            RespValue del1Resp = decoder.decode(in);
            assertEquals(ResponseType.INTEGER, del1Resp.getType());
            assertEquals(1L, del1Resp.getData());

            // EXISTS item after DEL
            out.write(encoder.encode("EXISTS", "item"));
            out.flush();
            RespValue existsAfterDelResp = decoder.decode(in);
            assertEquals(ResponseType.INTEGER, existsAfterDelResp.getType());
            assertEquals(0L, existsAfterDelResp.getData());

            // DEL item again
            out.write(encoder.encode("DEL", "item"));
            out.flush();
            RespValue del0Resp = decoder.decode(in);
            assertEquals(ResponseType.INTEGER, del0Resp.getType());
            assertEquals(0L, del0Resp.getData());
        }
    }

    @Test
    void testMultipleSequentialRequests() throws Exception {
        try (Socket socket = new Socket("127.0.0.1", port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            for (int i = 0; i < 50; i++) {
                String key = "k" + i;
                String val = "v" + i;

                out.write(encoder.encode("SET", key, val));
                out.flush();
                RespValue setRes = decoder.decode(in);
                assertEquals("OK", setRes.getData());

                out.write(encoder.encode("GET", key));
                out.flush();
                RespValue getRes = decoder.decode(in);
                assertEquals(val, getRes.getData());
            }
        }
    }

    @Test
    void testConcurrentClients() throws Exception {
        int clientCount = 10;
        int requestsPerClient = 30;
        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int c = 0; c < clientCount; c++) {
            final int clientId = c;
            tasks.add(() -> {
                try (Socket socket = new Socket("127.0.0.1", port);
                     OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {
                    CommandEncoder localEncoder = new CommandEncoder();
                    ResponseDecoder localDecoder = new ResponseDecoder();

                    for (int r = 0; r < requestsPerClient; r++) {
                        String key = "client_" + clientId + "_key_" + r;
                        String val = "val_" + r;

                        out.write(localEncoder.encode("SET", key, val));
                        out.flush();
                        RespValue setRes = localDecoder.decode(in);
                        if (!"OK".equals(setRes.getData())) {
                            return false;
                        }

                        out.write(localEncoder.encode("GET", key));
                        out.flush();
                        RespValue getRes = localDecoder.decode(in);
                        if (!val.equals(getRes.getData())) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }

        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        for (Future<Boolean> f : futures) {
            assertTrue(f.get());
        }
    }
}
