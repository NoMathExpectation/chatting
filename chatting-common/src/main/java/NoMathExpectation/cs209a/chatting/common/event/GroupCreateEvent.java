package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

@Value
public class GroupCreateEvent implements Event {
    @NonNull String name;
    @NonNull User owner;

    public static final EventKey<GroupCreateEvent> key = new EventKey<>() {
        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public @NonNull Class<GroupCreateEvent> getEventClass() {
            return GroupCreateEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "GroupCreateEvent";
        }

        @Override
        public void encode(@NonNull GroupCreateEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeUTF(event.name);
            stream.writeUTF(event.owner.getId().toString());
        }

        @Override
        public @NonNull GroupCreateEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            val name = stream.readUTF();
            val owner = Connector.getInstance().getContacts().get(UUID.fromString(stream.readUTF()));
            if (!(owner instanceof User)) {
                throw new IllegalArgumentException("Owner is not a user.");
            }

            return new GroupCreateEvent(name, (User) owner);
        }
    };
}
