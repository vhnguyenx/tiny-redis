package com.tinyredis.core;

import com.tinyredis.protocol.Command;

public class TinyRedisEngine {

    private final KeyValueStore store;

    public TinyRedisEngine(KeyValueStore store) {
        this.store = store;
    }

    public EngineResult execute(Command command) {
        return switch (command.getType()) {
            case SET -> executeSet(command);
            case GET -> executeGet(command);
            case DEL -> executeDelete(command);
            case EXISTS -> executeExists(command);
        };
    }

    private EngineResult executeSet(Command command) {
        String key = command.getArguments().get(0);
        String data = command.getArguments().get(1);

        Value value = new Value(ValueType.STRING, data);

        store.set(key, value);

        return EngineResult.success();
    }

    private EngineResult executeGet(Command command) {
        String key = command.getArguments().get(0);

        Value value = store.get(key);

        if (value == null) {
            return EngineResult.missing();
        }

        return EngineResult.value(value);
    }

    private EngineResult executeDelete(Command command) {
        String key = command.getArguments().get(0);

        return EngineResult.integer(store.delete(key) ? 1 : 0);
    }

    private EngineResult executeExists(Command command) {
        String key = command.getArguments().get(0);

        return EngineResult.integer(store.contains(key) ? 1 : 0);
    }
}
