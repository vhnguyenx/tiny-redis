package com.tinyredis.expiration;

public class SystemTimeSource implements TimeSource {

    @Override
    public long now() {
        return System.currentTimeMillis();
    }

}
