package com.tinyredis.server;

public final class TinyRedisServerMain {
    private TinyRedisServerMain() {
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6379;
        new TinyRedisServer(port).start();
    }
}