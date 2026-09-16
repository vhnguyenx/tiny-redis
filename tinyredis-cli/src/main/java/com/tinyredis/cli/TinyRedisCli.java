package com.tinyredis.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.tinyredis.protocol.CommandEncoder;
import com.tinyredis.protocol.RespValue;
import com.tinyredis.protocol.ResponseDecoder;
import com.tinyredis.protocol.ResponseType;

public final class TinyRedisCli {
    private TinyRedisCli() {
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 6379;

        CommandEncoder commandEncoder = new CommandEncoder();
        ResponseDecoder responseDecoder = new ResponseDecoder();

        try (Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                OutputStream output = socket.getOutputStream()) {
            while (true) {
                System.out.print(host + ":" + port + "> ");
                String line = console.readLine();
                if (line == null || line.equalsIgnoreCase("quit")) {
                    break;
                }
                if (line.isBlank()) {
                    continue;
                }

                String[] arguments = Arrays.stream(line.trim().split("\\s+"))
                    .toArray(String[]::new);
                arguments[0] = arguments[0].toUpperCase();

                output.write(commandEncoder.encode(arguments));
                output.flush();
                print(responseDecoder.decode(socket.getInputStream()));
            }
        }
    }

    private static void print(RespValue response) {
        if (response.getType() == ResponseType.NULL) {
            System.out.println("(nil)");
            return;
        }
        System.out.println(response.getData());
    }
}
