package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import NoMathExpectation.cs209a.chatting.common.event.EventManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class ClientConnectorImpl extends Connector {
    private final @NonNull Socket socket;

    @Override
    public Map<UUID, Contact> getContacts() {
        return ServerConnectorImpl.getInstance().getContacts();
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
        this.incoming = socket.getInputStream();
        this.outgoing = socket.getOutputStream();
    }
}
