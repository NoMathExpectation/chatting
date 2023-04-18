package NoMathExpectation.cs209a.chatting.client.contact;

import NoMathExpectation.cs209a.chatting.common.contact.User;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserImpl extends User {
    public UserImpl(@NonNull String name) {
        this(UUID.randomUUID(), name);
    }

    public UserImpl(@NonNull UUID id, @NonNull String name) {
        super(id, name);
    }

    public UserImpl(@NonNull User user) {
        this(user.getId(), user.getName());
    }

    @Override
    public void sendMessage(String message) {

    }
}
