package com.tinyredis.core;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.tinyredis.protocol.Command;
import com.tinyredis.protocol.CommandType;

public class RedisEngineTest {

    @Test
    void executeSetStoresValueAndReturnsStoredString() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);

        EngineResult result = engine.execute(command(CommandType.SET, "name", "tinyredis"));

        assertEquals(ResultType.SUCCESS, result.getType());
        assertNull(result.getData());
        assertEquals(ValueType.STRING, store.get("name").getType());
        assertEquals("tinyredis", store.get("name").getData());
    }

    @Test
    void executeSetOverwritesExistingValue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);

        engine.execute(command(CommandType.SET, "name", "old"));
        EngineResult result = engine.execute(command(CommandType.SET, "name", "new"));

        assertEquals(ResultType.SUCCESS, result.getType());
        assertNull(result.getData());
        assertEquals("new", store.get("name").getData());
    }

    @Test
    void executeGetReturnsExistingValue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Value(ValueType.STRING, "tinyredis"));

        EngineResult result = engine.execute(command(CommandType.GET, "name"));

        assertEquals(ResultType.VALUE, result.getType());
        Value resultValue = (Value) result.getData();
        assertEquals(ValueType.STRING, resultValue.getType());
        assertEquals("tinyredis", resultValue.getData());
    }

    @Test
    void executeGetReturnsNullWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(command(CommandType.GET, "missing"));

        assertEquals(ResultType.MISSING, result.getType());
        assertNull(result.getData());
    }

    @Test
    void executeDeleteRemovesExistingKeyAndReturnsTrue() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Value(ValueType.STRING, "tinyredis"));

        EngineResult result = engine.execute(command(CommandType.DEL, "name"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(1L, result.getData());
        assertEquals(false, store.contains("name"));
    }

    @Test
    void executeDeleteReturnsFalseWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(command(CommandType.DEL, "missing"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(0L, result.getData());
    }

    @Test
    void executeExistsReturnsTrueWhenKeyExists() {
        KeyValueStore store = new KeyValueStore();
        TinyRedisEngine engine = new TinyRedisEngine(store);
        store.set("name", new Value(ValueType.STRING, "tinyredis"));

        EngineResult result = engine.execute(command(CommandType.EXISTS, "name"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(1L, result.getData());
    }

    @Test
    void executeExistsReturnsFalseWhenKeyDoesNotExist() {
        TinyRedisEngine engine = new TinyRedisEngine(new KeyValueStore());

        EngineResult result = engine.execute(command(CommandType.EXISTS, "missing"));

        assertEquals(ResultType.INTEGER, result.getType());
        assertEquals(0L, result.getData());
    }

    private static Command command(CommandType type, String... arguments) {
        return new Command(type, List.of(arguments));
    }
}
