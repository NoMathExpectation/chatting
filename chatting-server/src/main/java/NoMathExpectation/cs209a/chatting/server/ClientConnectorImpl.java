package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.*;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventManager;
import NoMathExpectation.cs209a.chatting.server.contact.GroupImpl;
import NoMathExpectation.cs209a.chatting.server.contact.UserImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j(topic = "ClientConnector")
public final class ClientConnectorImpl extends Connector {
    private final @NonNull Socket socket;

    @Getter
    private User user;

    @Override
    public Map<UUID, Contact> getContacts() {
        return getInstance().getContacts();
    }

    @Override
    @Synchronized
    public void sendEvent(@NonNull Event event) throws IOException {
        log.info("Sending event to client {}: {}", socket.getRemoteSocketAddress(), event);
        val key = EventManager.keyOf(event);
        outgoing.writeUTF(key.getId());
        key.encode(event, outgoing);
        outgoing.flush();
    }

    public @NonNull User newUser(@NonNull UUID id, @NonNull String name) {
        return new UserImpl(this, id, name);
    }

    public @NonNull Group newGroup(@NonNull UUID id, @NonNull String name, @NonNull User owner) {
        return new GroupImpl(id, name, owner);
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
        try {
            this.incoming = new ObjectInputStream(socket.getInputStream());
            val protocolEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            val hash = EventManager.hash();

            this.outgoing = new ObjectOutputStream(socket.getOutputStream());
            if (!(protocolEvent instanceof ProtocolEvent && ((ProtocolEvent) protocolEvent).getHash() == hash)) {
                sendEvent(new ResultEvent(-1, "Protocol mismatch, please update your client."));
                close();
                return;
            }
            sendEvent(new ResultEvent(0, "Protocol matched."));

            val loginEvent = EventManager.keyOf(incoming.readUTF()).decode(incoming);
            if (!(loginEvent instanceof LoginEvent)) {
                sendEvent(new ResultEvent(-2, "You should login before sending events."));
                close();
                return;
            }
            if (((LoginEvent) loginEvent).getName().isBlank()) {
                sendEvent(new ResultEvent(-3, "Name must not be blank."));
                close();
                return;
            }
            if (getContacts().values().parallelStream().anyMatch(x -> x.getName().equals(((LoginEvent) loginEvent).getName()))) {
                sendEvent(new ResultEvent(-3, "There is already a user named " + ((LoginEvent) loginEvent).getName() + "."));
                close();
                return;
            }

            user = new UserImpl(this, ((LoginEvent) loginEvent).getName());
            sendEvent(new ResultEvent(0, user.getId().toString()));

            val users = getContacts().values()
                    .parallelStream()
                    .filter(x -> x instanceof User)
                    .collect(Collectors.toMap(Contact::getId, x -> x));
            sendEvent(new ContactsEvent(users));

            getContacts().put(user.getId(), user);
            UserLoginEvent.key.broadcast(new UserLoginEvent(user.getId(), user.getName()));

            log.info("Client from {} with name {}, uuid {} is now online.", socket.getRemoteSocketAddress(), user.getName(), user.getId());

            while (isConnected()) {
                val eventName = incoming.readUTF();
                val key = EventManager.keyOf(eventName);
                key.decodeAndBroadcast(incoming);
            }
        } catch (Exception e) {
            //log.debug("Exception thrown when handling client connection from " + socket.getRemoteSocketAddress(), e);
        } finally {
            try {
                close();
            } catch (IOException ignored) {
            }

            if (user != null) {
                getContacts().remove(user.getId());
                UserLogoutEvent.key.broadcast(new UserLogoutEvent(user.getId()));
                getContacts().forEach((uuid, contact) -> {
                    if (contact instanceof Group) {
                        ((Group) contact).getMembers().remove(user);
                    }
                });
                getContacts().forEach((uuid, contact) -> {
                    if (contact instanceof Group && ((Group) contact).getMembers().isEmpty()) {
                        getContacts().remove(contact.getId());
                    }
                });
            }

            if (user == null) {
                log.info("Client from {} disconnected.", socket.getRemoteSocketAddress());
            } else {
                log.info("Client from {} with name {}, uuid {} disconnected.", socket.getRemoteSocketAddress(), user.getName(), user.getId());
            }
        }
    }
}
