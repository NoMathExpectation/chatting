package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Value
public class LoginEvent implements Event {
    @NonNull String name;

    public static final EventKey<LoginEvent> key = new EventKey<>() {
        @Getter
        private final int version = 0;

        @Override
        public @NonNull Class<LoginEvent> getEventClass() {
            return LoginEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "LoginEvent";
        }

        @Override
        public void encode(@NonNull LoginEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeUTF(event.name);
        }

        @Override
        public @NonNull LoginEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            return new LoginEvent(stream.readUTF());
        }
    };
}
