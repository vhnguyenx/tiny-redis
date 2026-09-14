package com.tinyredis.core;

public class EngineResult {
    private final Value value;

    public EngineResult(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
