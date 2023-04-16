package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import lombok.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.UUID;

@Value
public class MessageEvent implements Event {
    @NonNull Instant time;
    @NonNull User sentBy;
    @NonNull Contact sendTo;
    @NonNull String message;

    public static final EventKey<MessageEvent> key = new EventKey<>() {
        @Getter
        private final int version = 0;

        @Override
        public @NonNull Class<MessageEvent> getEventClass() {
            return MessageEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "MessageEvent";
        }

        @Override
        @SneakyThrows
        public void encode(@NonNull MessageEvent event, @NonNull ObjectOutputStream stream) {
            stream.writeUTF(event.time.toString());
            stream.writeUTF(event.sentBy.getId().toString());
            stream.writeUTF(event.sendTo.getId().toString());
            stream.writeUTF(event.message);
        }

        @Override
        @SneakyThrows
        public @NonNull MessageEvent decode(@NonNull ObjectInputStream stream) {
            val time = Instant.parse(stream.readUTF());
            val sentBy = Connector.getInstance().getContacts().get(UUID.fromString(stream.readUTF()));
            val sendTo = Connector.getInstance().getContacts().get(UUID.fromString(stream.readUTF()));
            val message = stream.readUTF();

            if (!(sentBy instanceof User && sendTo != null)) {
                throw new IllegalArgumentException("Invalid MessageEvent received.");
            }
            return new MessageEvent(time, (User) sentBy, sendTo, message);
        }
    };
}
