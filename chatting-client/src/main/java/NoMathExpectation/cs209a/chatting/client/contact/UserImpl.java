package NoMathExpectation.cs209a.chatting.client.contact;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserImpl extends User {
    public UserImpl(@NonNull UUID id, @NonNull String name) {
        super(id, name);
    }

    @Override
    @SneakyThrows
    public void sendMessage(MessageEvent messageEvent) {
        Connector.getInstance().sendEvent(messageEvent);
    }

    @Override
    public @NonNull MessageEvent sendMessage(String message) {
        val messageEvent = new MessageEvent(Instant.now(), ConnectorImpl.getClientInstance().getUser(), this, message);
        sendMessage(messageEvent);
        return messageEvent;
    }
}
