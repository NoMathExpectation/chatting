package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import NoMathExpectation.cs209a.chatting.common.event.EventManager;
import NoMathExpectation.cs209a.chatting.common.event.ProtocolEvent;
import NoMathExpectation.cs209a.chatting.common.event.ResultEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        this.incoming = new ObjectInputStream(socket.getInputStream());
        this.outgoing = new ObjectOutputStream(socket.getOutputStream());

        val protocolEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
        val hash = EventManager.hash();
        if (!(protocolEvent instanceof ProtocolEvent && ((ProtocolEvent) protocolEvent).hash != hash)) {
            ResultEvent.key.encode(new ResultEvent(-1, "Protocol mismatch, please update your client."), outgoing);
            outgoing.flush();
            close();
            return;
        }

        ResultEvent.key.encode(new ResultEvent(0, "Protocol matched."), outgoing);
        outgoing.flush();
    }
}
