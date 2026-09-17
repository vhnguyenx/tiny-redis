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
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tinyredis.protocol.CommandEncoder;
import com.tinyredis.protocol.ResponseDecoder;
import com.tinyredis.protocol.RespValue;
import com.tinyredis.protocol.ResponseType;

class TinyRedisConcurrencyStressTest {

    private TinyRedisServer server;
    private Thread serverThread;
    private int port;

    @BeforeEach
    void setUp() throws Exception {
        server = new TinyRedisServer(0);
        port = server.getPort();

        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                // Server stopped
            }
        });
        serverThread.start();
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
    void testSharedKeyConcurrentAccess() throws Exception {
        int threadCount = 30;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            tasks.add(() -> {
                try (Socket socket = new Socket("127.0.0.1", port);
                     OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {

                    CommandEncoder encoder = new CommandEncoder();
                    ResponseDecoder decoder = new ResponseDecoder();

                    for (int op = 0; op < operationsPerThread; op++) {
                        String sharedKey = "shared_key_" + (op % 5);
                        String value = "val_from_thread_" + threadId + "_op_" + op;

                        // SET
                        out.write(encoder.encode("SET", sharedKey, value));
                        out.flush();
                        RespValue setResp = decoder.decode(in);
                        if (setResp.getType() == ResponseType.SIMPLE_STRING && "OK".equals(setResp.getData())) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }

                        // GET
                        out.write(encoder.encode("GET", sharedKey));
                        out.flush();
                        RespValue getResp = decoder.decode(in);
                        if (getResp.getType() == ResponseType.BULK_STRING || getResp.getType() == ResponseType.NULL) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }

                        // EXISTS
                        out.write(encoder.encode("EXISTS", sharedKey));
                        out.flush();
                        RespValue existsResp = decoder.decode(in);
                        if (existsResp.getType() == ResponseType.INTEGER) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
                return null;
            });
        }

        long startTime = System.currentTimeMillis();
        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        boolean finished = executor.awaitTermination(15, TimeUnit.SECONDS);
        long elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(finished, "All concurrent tasks should complete within timeout");
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent shared key access");
        assertEquals(threadCount * operationsPerThread * 3, successCount.get());

        System.out.printf("⚡ Concurrent Shared Key Test Passed! Total Operations: %d, Time: %d ms (%.2f ops/sec)%n",
                successCount.get(), elapsedTime, (successCount.get() * 1000.0) / elapsedTime);
    }

    @Test
    void test50ConcurrentClientsHeavyLoad() throws Exception {
        int clientCount = 50;
        int requestsPerClient = 100; // 50 * 100 * 2 = 10,000 total commands
        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        AtomicInteger totalSuccess = new AtomicInteger(0);

        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int c = 0; c < clientCount; c++) {
            final int clientId = c;
            tasks.add(() -> {
                try (Socket socket = new Socket("127.0.0.1", port);
                     OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {

                    CommandEncoder encoder = new CommandEncoder();
                    ResponseDecoder decoder = new ResponseDecoder();

                    for (int req = 0; req < requestsPerClient; req++) {
                        String key = "client_" + clientId + "_key_" + req;
                        String value = "data_" + req;

                        // 1. SET
                        out.write(encoder.encode("SET", key, value));
                        out.flush();
                        RespValue setRes = decoder.decode(in);
                        if (!"OK".equals(setRes.getData())) return false;
                        totalSuccess.incrementAndGet();

                        // 2. GET & verify
                        out.write(encoder.encode("GET", key));
                        out.flush();
                        RespValue getRes = decoder.decode(in);
                        if (!value.equals(getRes.getData())) return false;
                        totalSuccess.incrementAndGet();
                    }
                    return true;
                }
            });
        }

        long start = System.currentTimeMillis();
        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        assertTrue(executor.awaitTermination(20, TimeUnit.SECONDS));
        long duration = System.currentTimeMillis() - start;

        for (Future<Boolean> f : futures) {
            assertTrue(f.get(), "Each client execution must succeed");
        }

        int expectedOps = clientCount * requestsPerClient * 2;
        assertEquals(expectedOps, totalSuccess.get());

        System.out.printf("🚀 50 Clients Heavy Load Test Passed! Total Operations: %d, Time: %d ms (%.2f ops/sec)%n",
                totalSuccess.get(), duration, (totalSuccess.get() * 1000.0) / duration);
    }
}
