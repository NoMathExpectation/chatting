package NoMathExpectation.cs209a.chatting.client;

import NoMathExpectation.cs209a.chatting.client.contact.Contacts;
import NoMathExpectation.cs209a.chatting.client.gui.Chat;
import NoMathExpectation.cs209a.chatting.client.gui.Login;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.ContactsEvent;
import NoMathExpectation.cs209a.chatting.common.event.LoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.ProtocolEvent;
import NoMathExpectation.cs209a.chatting.common.event.ResultEvent;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j(topic = "Connector")
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
    @Synchronized
    public void sendEvent(@NonNull Event event) throws IOException {
        log.info("Sending event to server: {}", event);
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

    private static void disconnectedCallback(Event e, String defaultReason) {
        getInstance().getContacts().clear();

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
        log.info("Connecting to {}:{} ...", host, port);

        Platform.runLater(() -> Login.setStatusText(""));

        try {
            socket.connect(new InetSocketAddress(host, port));

            this.outgoing = new ObjectOutputStream(socket.getOutputStream());
            outgoing.writeUTF(ProtocolEvent.key.getId());
            ProtocolEvent.key.encode(new ProtocolEvent(EventManager.hash()), outgoing);
            outgoing.flush();

            this.incoming = new ObjectInputStream(socket.getInputStream());
            val resultEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            if (!(resultEvent instanceof ResultEvent && ((ResultEvent) resultEvent).getResult() == 0)) {
                disconnectedCallback(resultEvent, "Please update your client.");
                close();
                return;
            }

            outgoing.writeUTF(LoginEvent.key.getId());
            LoginEvent.key.encode(new LoginEvent(name), outgoing);
            outgoing.flush();
            val resultEvent2 = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            if (!(resultEvent2 instanceof ResultEvent && ((ResultEvent) resultEvent2).getResult() == 0)) {
                disconnectedCallback(resultEvent2, "Login Failed.");
                close();
                return;
            }

            id = UUID.fromString(((ResultEvent) resultEvent2).getReason());

            val users = ContactsEvent.key
                    .decode(incoming)
                    .getContacts()
                    .values()
                    .parallelStream()
                    .map(Contacts::of)
                    .collect(Collectors.toMap(Contact::getId, x -> x));
            getContacts().clear();
            getContacts().putAll(users);

            Platform.runLater(() -> {
                Chat.connectedCallback(host, port, name, id.toString(), FXCollections.observableArrayList(users.values()));
                Login.loginSuccessCallback();
            });

            while (isConnected()) {
                val eventName = incoming.readUTF();
                val key = EventManager.keyOf(eventName);
                key.decodeAndBroadcast(incoming);
            }

            disconnectedCallback(null, "Disconnected.");
        } catch (Exception e) {
            disconnectedCallback(null, e.getMessage());
        } finally {
            try {
                close();
            } catch (IOException ignored) {
            }
            log.info("Disconnected.");
        }
    }
}
