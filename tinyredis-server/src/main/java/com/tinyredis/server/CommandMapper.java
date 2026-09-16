package com.tinyredis.server;

import com.tinyredis.core.Operation;
import com.tinyredis.core.OperationType;
import com.tinyredis.protocol.Command;

public final class CommandMapper {
    public Operation map(Command command) {
        return new Operation(
                OperationType.valueOf(command.getType().name()),
                command.getArguments());
    }
}