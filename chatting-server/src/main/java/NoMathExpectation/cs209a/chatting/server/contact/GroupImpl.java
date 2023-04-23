package NoMathExpectation.cs209a.chatting.server.contact;

import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class GroupImpl extends Group {
    public GroupImpl(@NonNull UUID id, @NonNull String name, @NonNull User owner) {
        super(id, name, owner);
    }

    @Override
    @SneakyThrows
    public void sendMessage(MessageEvent messageEvent) {
        for (User member : members) {
            ((UserImpl) member).getClient().sendEvent(messageEvent);
        }
    }

    @Override
    public @NonNull MessageEvent sendMessage(String message) {
        throw new UnsupportedOperationException("Server itself cannot send message.");
    }
}
