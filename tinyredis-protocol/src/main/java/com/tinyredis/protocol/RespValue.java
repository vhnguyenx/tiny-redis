package com.tinyredis.protocol;

public final class RespValue {
    private final ResponseType type;
    private final Object data;

    public RespValue(ResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ResponseType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}