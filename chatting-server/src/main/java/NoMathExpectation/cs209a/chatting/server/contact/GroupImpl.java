package NoMathExpectation.cs209a.chatting.server.contact;

import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.UUID;

public class GroupImpl extends Group {
    public GroupImpl(@NonNull UUID id, @NonNull String name) {
        super(id, name);
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
