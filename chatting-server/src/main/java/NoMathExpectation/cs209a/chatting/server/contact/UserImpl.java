package NoMathExpectation.cs209a.chatting.server.contact;

import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.server.ClientConnectorImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

@ToString
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

    public UserImpl(@NonNull ClientConnectorImpl client, @NonNull User user) {
        this(client, user.getId(), user.getName());
    }

    @Override
    public void sendMessage(String message) {

    }
}
