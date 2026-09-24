package com.tinyredis.core;

public class Entry {
    private final Value value;
    private final long expireAt;

    public Entry(Value value, long expireAt) {
        this.value = value;
        this.expireAt = expireAt;
    }

    public Entry(Value value) {
        this(value, -1L);
    }

    public Value getValue() {
        return value;
    }

    public long getExpireAt() {
        return expireAt;
    }
}
