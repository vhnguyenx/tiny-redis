package com.tinyredis.core;

public class TinyRedisEngine {

    private final KeyValueStore store;

    public TinyRedisEngine(KeyValueStore store) {
        this.store = store;
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
        String key = operation.getArguments().get(0);
        String data = operation.getArguments().get(1);

        Value value = new Value(ValueType.STRING, data);

        store.set(key, value);

        return EngineResult.success();
    }

    private EngineResult executeGet(Operation operation) {
        String key = operation.getArguments().get(0);

        Value value = store.get(key);

        if (value == null) {
            return EngineResult.missing();
        }

        return EngineResult.value(value);
    }

    private EngineResult executeDelete(Operation operation) {
        String key = operation.getArguments().get(0);

        return EngineResult.integer(store.delete(key) ? 1 : 0);
    }

    private EngineResult executeExists(Operation operation) {
        String key = operation.getArguments().get(0);

        return EngineResult.integer(store.contains(key) ? 1 : 0);
    }
}
