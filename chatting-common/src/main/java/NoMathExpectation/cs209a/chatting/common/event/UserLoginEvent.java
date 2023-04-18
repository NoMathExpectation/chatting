package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

@Value
public class UserLoginEvent implements Event {
    @NonNull UUID id;
    @NonNull String name;

    public static final EventKey<UserLoginEvent> key = new EventKey<>() {
        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public @NonNull Class<UserLoginEvent> getEventClass() {
            return UserLoginEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "UserLoginEvent";
        }

        @Override
        public void encode(@NonNull UserLoginEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeUTF(event.getId().toString());
            stream.writeUTF(event.getName());
        }

        @Override
        public @NonNull UserLoginEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            return new UserLoginEvent(UUID.fromString(stream.readUTF()), stream.readUTF());
        }
    };
}
