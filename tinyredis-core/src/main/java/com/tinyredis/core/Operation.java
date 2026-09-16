package com.tinyredis.core;

import java.util.List;

public final class Operation {
    private final OperationType type;
    private final List<String> arguments;

    public Operation(OperationType type, List<String> arguments) {
        this.type = type;
        this.arguments = List.copyOf(arguments);
    }

    public OperationType getType() {
        return type;
    }

    public List<String> getArguments() {
        return arguments;
    }
}