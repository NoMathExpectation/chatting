package NoMathExpectation.cs209a.chatting.client;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import NoMathExpectation.cs209a.chatting.common.event.EventManager;
import NoMathExpectation.cs209a.chatting.common.event.ProtocolEvent;
import NoMathExpectation.cs209a.chatting.common.event.ResultEvent;
import javafx.application.Platform;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        this.incoming = new ObjectInputStream(socket.getInputStream());
        this.outgoing = new ObjectOutputStream(socket.getOutputStream());

        ProtocolEvent.key.encode(new ProtocolEvent(EventManager.hash()), outgoing);
        outgoing.flush();
        val resultEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
        if (!(resultEvent instanceof ResultEvent && ((ResultEvent) resultEvent).result == 0)) {
            Platform.runLater(() -> {
            }); // TODO: 2023/4/15 pop a dialog
            socket.close();
            return;
        }


    }
}
