package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@Slf4j(topic = "ServerConnector")
public final class ServerConnectorImpl extends Connector {
    private final int port;

    private final @NonNull ServerSocket serverSocket = new ServerSocket();

    public ServerConnectorImpl(int port) throws IOException {
        this.port = port;
        instance = this;
    }

    @Override
    public void sendEvent(@NonNull Event event) {
        throw new UnsupportedOperationException("Server itself cannot send events.");
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public boolean isConnected() {
        return serverSocket.isBound() && !serverSocket.isClosed();
    }

    @Override
    @SneakyThrows
    public void run() {
        try {
            serverSocket.bind(new InetSocketAddress(port));

            while (!serverSocket.isClosed()) {
                try {
                    val s = serverSocket.accept();
                    ClientConnectorImpl clientConnector = new ClientConnectorImpl(s);
                    val clientThread = new Thread(clientConnector);
                    clientThread.setDaemon(true);
                    clientThread.setUncaughtExceptionHandler((thread, throwable) -> {
                        log.error("Client thread uncaught error: ", throwable);
                        getContacts().remove(clientConnector.getUser().getId());
                        try {
                            clientConnector.close();
                        } catch (IOException ignored) {
                        }
                    });
                    clientThread.start();
                } catch (Exception e) {
                    log.error("Server socket error: ", e);
                }
            }
        } finally {
            serverSocket.close();
        }
    }
}
