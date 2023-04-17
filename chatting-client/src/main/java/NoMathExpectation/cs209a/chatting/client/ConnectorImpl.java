package NoMathExpectation.cs209a.chatting.client;

import NoMathExpectation.cs209a.chatting.client.gui.Chat;
import NoMathExpectation.cs209a.chatting.client.gui.Login;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.LoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.ProtocolEvent;
import NoMathExpectation.cs209a.chatting.common.event.ResultEvent;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventManager;
import javafx.application.Platform;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public final class ConnectorImpl extends Connector {
    private final @NonNull String host;
    private final int port;

    @Getter
    private final @NonNull String name;

    @Getter
    private UUID id;

    private final @NonNull Socket socket = new Socket();

    public ConnectorImpl(@NonNull String host, int port, @NonNull String name) {
        this.host = host;
        this.port = port;
        this.name = name;
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

    private static void disconnectedCallback(Event e, @NonNull String defaultReason) {
        String reason;
        if (e instanceof ResultEvent) {
            reason = ((ResultEvent) e).getReason();
        } else {
            reason = defaultReason;
        }
        Platform.runLater(() -> {
            if (Login.isLoggingIn()) {
                Login.loginFailedCallback(reason);
            } else {
                Chat.disconnectedCallback(reason);
            }
        });
    }

    @Override
    @SneakyThrows
    public void run() {
        try {
            socket.connect(new InetSocketAddress(host, port));
            this.incoming = new ObjectInputStream(socket.getInputStream());
            this.outgoing = new ObjectOutputStream(socket.getOutputStream());

            ProtocolEvent.key.encode(new ProtocolEvent(EventManager.hash()), outgoing);
            outgoing.flush();
            val resultEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            if (!(resultEvent instanceof ResultEvent && ((ResultEvent) resultEvent).getResult() == 0)) {
                disconnectedCallback(resultEvent, "Please update your client.");
                close();
                return;
            }

            LoginEvent.key.encode(new LoginEvent(name), outgoing);
            outgoing.flush();
            val resultEvent2 = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            if (!(resultEvent2 instanceof ResultEvent && ((ResultEvent) resultEvent2).getResult() == 0)) {
                disconnectedCallback(resultEvent2, "Login Failed.");
                close();
                return;
            }

            id = UUID.fromString(((ResultEvent) resultEvent2).getReason());
            Platform.runLater(() -> Chat.connectedCallback(host, port, name, id.toString()));

            while (isConnected()) {
                // TODO: 2023/4/18 transmission
            }
        } catch (Exception e) {
            disconnectedCallback(null, e.getMessage());
        } finally {
            try {
                close();
            } catch (IOException ignored) {
            }
        }
    }
}
