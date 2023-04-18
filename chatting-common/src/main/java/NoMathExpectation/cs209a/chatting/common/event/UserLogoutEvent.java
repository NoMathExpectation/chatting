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
public class UserLogoutEvent implements Event {
    @NonNull UUID id;

    public static final EventKey<UserLogoutEvent> key = new EventKey<>() {
        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public @NonNull Class<UserLogoutEvent> getEventClass() {
            return UserLogoutEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "UserLogoutEvent";
        }

        @Override
        public void encode(@NonNull UserLogoutEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeUTF(event.getId().toString());
        }

        @Override
        public @NonNull UserLogoutEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            return new UserLogoutEvent(UUID.fromString(stream.readUTF()));
        }
    };
}
