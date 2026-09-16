package com.tinyredis.server;

import com.tinyredis.core.EngineResult;
import com.tinyredis.core.ResultType;
import com.tinyredis.core.Value;
import com.tinyredis.protocol.RespValue;
import com.tinyredis.protocol.ResponseType;

public final class ResponseMapper {
    public RespValue map(EngineResult result) {
        return switch (result.getType()) {
            case SUCCESS -> new RespValue(ResponseType.SIMPLE_STRING, "OK");
            case VALUE -> new RespValue(ResponseType.BULK_STRING, valueData(result));
            case INTEGER -> new RespValue(ResponseType.INTEGER, result.getData());
            case MISSING -> new RespValue(ResponseType.NULL, null);
            case ERROR -> new RespValue(ResponseType.ERROR, result.getData());
        };
    }

    private String valueData(EngineResult result) {
        Value value = (Value) result.getData();
        return value.getData() == null ? null : value.getData().toString();
    }
}