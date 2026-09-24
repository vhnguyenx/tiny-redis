package com.tinyredis.core;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;


public class RedisEngineTest {

    @Test
    void executeSetStoresValueAndReturnsStoredString() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);

        EngineResult result = engine.execute(operation(OperationType.SET, "name", "tinyredis"));

        assertEquals(ResultType.SUCCESS, result.getType());
        assertNull(result.getData());
        assertEquals(ValueType.STRING, store.get("name").getValue().getType());
        assertEquals("tinyredis", store.get("name").getValue().getData());
    }

    @Test
    void executeSetOverwritesExistingValue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);

        engine.execute(operation(OperationType.SET, "name", "old"));
        EngineResult result = engine.execute(operation(OperationType.SET, "name", "new"));

        assertEquals(ResultType.SUCCESS, result.getType());
        assertNull(result.getData());
        assertEquals("new", store.get("name").getValue().getData());
    }

    @Test
    void executeGetReturnsExistingValue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Entry(new Value(ValueType.STRING, "tinyredis")));

        EngineResult result = engine.execute(operation(OperationType.GET, "name"));

        assertEquals(ResultType.VALUE, result.getType());
        Value resultValue = (Value) result.getData();
        assertEquals(ValueType.STRING, resultValue.getType());
        assertEquals("tinyredis", resultValue.getData());
    }

    @Test
    void executeGetReturnsNullWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(operation(OperationType.GET, "missing"));

        assertEquals(ResultType.MISSING, result.getType());
        assertNull(result.getData());
    }

    @Test
    void executeDeleteRemovesExistingKeyAndReturnsTrue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Entry(new Value(ValueType.STRING, "tinyredis")));

        EngineResult result = engine.execute(operation(OperationType.DEL, "name"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(1L, result.getData());
        assertEquals(false, store.contains("name"));
    }

    @Test
    void executeDeleteReturnsFalseWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(operation(OperationType.DEL, "missing"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(0L, result.getData());
    }

    @Test
    void executeExistsReturnsTrueWhenKeyExists() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Entry(new Value(ValueType.STRING, "tinyredis")));

        EngineResult result = engine.execute(operation(OperationType.EXISTS, "name"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(1L, result.getData());
    }

    @Test
    void executeExistsReturnsFalseWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(operation(OperationType.EXISTS, "missing"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(0L, result.getData());
    }

    private static Operation operation(OperationType type, String... arguments) {
        return new Operation(type, List.of(arguments));
    }
}
