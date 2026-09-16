package com.tinyredis.core;

public class Value {
    private final ValueType type;
    private final Object data;

    public Value(ValueType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ValueType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
