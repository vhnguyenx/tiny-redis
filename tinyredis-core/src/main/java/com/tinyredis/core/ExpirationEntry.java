package com.tinyredis.core;

public class ExpirationEntry {

    private final String key;
    private final long expireAt;

    public ExpirationEntry(String key, long expireAt) {
        this.key = key;
        this.expireAt = expireAt;
    }

    public String getKey() {
        return key;
    }

    public long getExpireAt() {
        return expireAt;
    }
}