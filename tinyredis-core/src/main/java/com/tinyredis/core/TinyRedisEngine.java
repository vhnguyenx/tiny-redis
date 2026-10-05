package com.tinyredis.core;

import com.tinyredis.expiration.ExpirationManager;
import com.tinyredis.expiration.SystemTimeSource;

public class TinyRedisEngine {

    private final KeyValueStore store;
    private final ExpirationManager expirationManager;
    private final SystemTimeSource timeSource;

    public TinyRedisEngine(KeyValueStore store) {
        this(store, new ExpirationManager(store), new SystemTimeSource());
    }

    public TinyRedisEngine(KeyValueStore store, ExpirationManager expirationManager, SystemTimeSource timeSource) {
        this.store = store;
        this.expirationManager = expirationManager;
        this.timeSource = timeSource;
    }

    public EngineResult execute(Operation operation) {
        return switch (operation.getType()) {
            case SET -> executeSet(operation);
            case GET -> executeGet(operation);
            case DEL -> executeDelete(operation);
            case EXISTS -> executeExists(operation);
        };
    }

    private EngineResult executeSet(Operation operation) {
        java.util.List<String> args = operation.getArguments();
        String key = args.get(0);
        String data = args.get(1);

        long expireAt = -1;

        for (int i = 2; i < args.size(); i++) {
            String arg = args.get(i).toUpperCase();
            if (arg.equals("EX") && i + 1 < args.size()) {
                long seconds = Long.parseLong(args.get(++i));
                expireAt = timeSource.now() + (seconds * 1000);
            } else if (arg.equals("PX") && i + 1 < args.size()) {
                long milliseconds = Long.parseLong(args.get(++i));
                expireAt = timeSource.now() + milliseconds;
            }
        }

        Value value = new Value(ValueType.STRING, data);

        store.set(key, new Entry(value, expireAt));
        
        if (expireAt != -1) {
            expirationManager.register(key, expireAt);
        }

        return EngineResult.success();
    }

    private EngineResult executeGet(Operation operation) {
        String key = operation.getArguments().get(0);

        Entry entry = store.get(key);

        if (entry == null) {
            return EngineResult.missing();
        }

        if (entry.getExpireAt() > 0 && entry.getExpireAt() <= timeSource.now()) {
            store.delete(key);
            return EngineResult.missing();
        }

        return EngineResult.value(entry.getValue());
    }

    private EngineResult executeDelete(Operation operation) {
        String key = operation.getArguments().get(0);

        return EngineResult.integer(store.delete(key) ? 1 : 0);
    }

    private EngineResult executeExists(Operation operation) {
        String key = operation.getArguments().get(0);
        
        Entry entry = store.get(key);
        if (entry == null) {
            return EngineResult.integer(0);
        }

        if (entry.getExpireAt() > 0 && entry.getExpireAt() <= timeSource.now()) {
            store.delete(key);
            return EngineResult.integer(0);
        }

        return EngineResult.integer(1);
    }
}
