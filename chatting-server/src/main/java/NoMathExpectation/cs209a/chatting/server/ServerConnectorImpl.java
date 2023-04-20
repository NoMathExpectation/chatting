package NoMathExpectation.cs209a.chatting.server;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.server.contact.GroupImpl;
import NoMathExpectation.cs209a.chatting.server.contact.UserImpl;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.function.Predicate;

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

    public static void sendEvent(@NonNull Event event, Predicate<? super Contact> filter) {
        Connector.getInstance()
                .getContacts()
                .values()
                .parallelStream()
                .filter(filter.and(x -> x instanceof UserImpl))
                .forEach(contact -> {
                    try {
                        ((UserImpl) contact).getClient().sendEvent(event);
                    } catch (IOException e) {
                        log.error("Error while sending event " + event + " to user " + contact + " : ", e);
                    }
                });
    }

    public @NonNull User newUser(@NonNull UUID id, @NonNull String name) {
        throw new UnsupportedOperationException("Users cannot be created without client.");
    }

    public @NonNull Group newGroup(@NonNull UUID id, @NonNull String name) {
        return new GroupImpl(id, name);
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
            log.info("Server started on port {}.", port);

            while (!serverSocket.isClosed()) {
                try {
                    val s = serverSocket.accept();
                    log.info("Client connected: {}.", s.getRemoteSocketAddress());
                    ClientConnectorImpl clientConnector = new ClientConnectorImpl(s);
                    val clientThread = new Thread(clientConnector);
                    clientThread.setDaemon(true);
                    clientThread.setUncaughtExceptionHandler((thread, throwable) -> {
                        log.error("Client thread uncaught error: ", throwable);
                        if (clientConnector.getUser() != null) {
                            getContacts().remove(clientConnector.getUser().getId());
                        }
                        try {
                            clientConnector.close();
                        } catch (IOException ignored) {
                        }
                    });
                    clientThread.start();
                } catch (Exception e) {
                    if (!serverSocket.isClosed()) {
                        log.error("Server socket error: ", e);
                    }
                }
            }
        } finally {
            try {
                close();
            } catch (IOException ignored) {
            }
            log.info("Server stopped.");
        }
    }
}
