package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class ServerConnectorImpl extends Connector {
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
    @SneakyThrows
    public void run() {
        serverSocket.bind(new InetSocketAddress(port));
    }
}
