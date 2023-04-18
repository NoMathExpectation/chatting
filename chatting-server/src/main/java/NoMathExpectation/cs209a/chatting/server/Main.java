package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.server.event.meta.EventHandlers;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Scanner;

@Slf4j(topic = "Main")
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            port = 2333;
        }

        EventHandlers.registerAllHandlers();

        log.info("Starting server...");
        val connector = new ServerConnectorImpl(port);
        val thread = new Thread(connector);
        thread.start();

        @Cleanup val sc = new Scanner(System.in);

        loop:
        while (true) {
            val line = sc.nextLine();
            switch (line) {
                case "stop":
                    break loop;
                default:
                    log.info("Unknown command: {}", line);
            }
        }

        connector.close();
        thread.join();
    }
}
