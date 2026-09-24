package com.tinyredis.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KeyValueStore {
    private final ConcurrentMap<String, Entry> store;

    public KeyValueStore() {
        this.store = new ConcurrentHashMap<>();
    }

    public void set(String key, Entry value) {
        store.put(key, value);
    }

    public Entry get(String key) {
        return store.get(key);
    }

    public boolean delete(String key) {
        return store.remove(key) != null;
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }
}
