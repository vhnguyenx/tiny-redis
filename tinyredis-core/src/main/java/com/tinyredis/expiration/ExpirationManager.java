package com.tinyredis.expiration;

import java.util.Comparator;

import com.tinyredis.core.Entry;
import com.tinyredis.core.ExpirationEntry;
import com.tinyredis.core.KeyValueStore;
import com.tinyredis.datastructure.MinHeap;

public class ExpirationManager {

    private final KeyValueStore store;
    private final MinHeap<ExpirationEntry> expirationHeap;
    private Thread worker;
    private volatile boolean running;
    private final SystemTimeSource timeSource;

    public ExpirationManager(KeyValueStore store) {
        this.store = store;
        this.timeSource = new SystemTimeSource();
        expirationHeap = new MinHeap<>(Comparator.comparingLong(ExpirationEntry::getExpireAt));
    }

    public void register(String key, long expireAt) {
        ExpirationEntry entry = new ExpirationEntry(key, expireAt);
        expirationHeap.add(entry);
    }

    public boolean isEmpty() {
        return expirationHeap.isEmpty();
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;

        worker = new Thread(() -> {
            while (running) {
                processExpiredEntry();
            }
        });

        worker.setName("tinyredis-expiration-worker");
        worker.start();
    }

    public void stop() {
        running = false;

        if (worker != null) {
            worker.interrupt();
        }
    }

    private void processExpiredEntry() {
        if (expirationHeap.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return;
        }

        ExpirationEntry entry = expirationHeap.peek();

        long now = timeSource.now();
        long delay = entry.getExpireAt() - now;

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return;
        }

        entry = expirationHeap.poll();

        processExpiration(entry);
    }

    private void processExpiration(ExpirationEntry entry) {
        Entry current = store.get(entry.getKey());

        if (current == null) {
            return;
        }

        if (current.getExpireAt() != entry.getExpireAt()) {
            return;
        }

        if (timeSource.now() >= entry.getExpireAt()) {
            store.delete(entry.getKey());
        }
    }
}
