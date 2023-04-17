package NoMathExpectation.cs209a.chatting.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            port = 2333;
        }

        log.info("Starting server");
        val connector = new ServerConnectorImpl(port);
        val thread = new Thread(connector);
        thread.start();
        thread.join();
    }
}
