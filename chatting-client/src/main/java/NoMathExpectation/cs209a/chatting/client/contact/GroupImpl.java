package NoMathExpectation.cs209a.chatting.client.contact;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.time.Instant;
import java.util.UUID;

public class GroupImpl extends Group {
    public GroupImpl(@NonNull UUID id, @NonNull String name) {
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
