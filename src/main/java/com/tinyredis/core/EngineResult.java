package com.tinyredis.core;

public class EngineResult {
    private final ResultType type;
    private final Object data;

    public EngineResult(ResultType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static EngineResult success() {
        return new EngineResult(ResultType.SUCCESS, null);
    }

    public static EngineResult value(Value value) {
        return new EngineResult(ResultType.VALUE, value);
    }

    public static EngineResult integer(long value) {
        return new EngineResult(ResultType.INTEGER, value);
    }

    public static EngineResult missing() {
        return new EngineResult(ResultType.MISSING, null);
    }

    public static EngineResult error(String message) {
        return new EngineResult(ResultType.ERROR, message);
    }

    public ResultType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
