package NoMathExpectation.cs209a.chatting.client;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import NoMathExpectation.cs209a.chatting.common.event.EventManager;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ConnectorImpl extends Connector {
    private final @NonNull String host;
    private final int port;

    private final @NonNull Socket socket = new Socket();

    public ConnectorImpl(@NonNull String host, int port) {
        this.host = host;
        this.port = port;
        instance = this;
    }

    @Override
    public void sendEvent(@NonNull Event event) throws IOException {
        val out = new ObjectOutputStream(outgoing);
        val key = EventManager.keyOf(event);
        out.writeUTF(key.getId());
        key.encode(event, out);
        outgoing.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    @SneakyThrows
    public void run() {
        socket.connect(new InetSocketAddress(host, port));
        this.incoming = socket.getInputStream();
        this.outgoing = socket.getOutputStream();
    }
}
