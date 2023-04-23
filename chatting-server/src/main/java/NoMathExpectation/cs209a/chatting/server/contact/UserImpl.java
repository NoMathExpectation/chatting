package NoMathExpectation.cs209a.chatting.server.contact;

import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import NoMathExpectation.cs209a.chatting.server.ClientConnectorImpl;
import lombok.*;

import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserImpl extends User {
    @Getter
    private final ClientConnectorImpl client;

    public UserImpl(@NonNull ClientConnectorImpl client, @NonNull UUID id, @NonNull String name) {
        super(id, name);
        this.client = client;
    }

    public UserImpl(@NonNull ClientConnectorImpl client, @NonNull String name) {
        this(client, UUID.randomUUID(), name);
    }

    @Override
    @SneakyThrows
    public void sendMessage(MessageEvent messageEvent) {
        client.sendEvent(messageEvent);
    }

    @Override
    public @NonNull MessageEvent sendMessage(String message) {
        throw new UnsupportedOperationException("Server itself cannot send message.");
    }
}
