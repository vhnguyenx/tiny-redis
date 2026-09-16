package com.tinyredis.protocol;

public enum ResponseType {
    SIMPLE_STRING,
    ERROR,
    INTEGER,
    BULK_STRING,
    NULL,
    ARRAY
}