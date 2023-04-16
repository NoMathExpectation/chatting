package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Value
public class ProtocolEvent implements Event {
    int hash;

    public static final EventKey<ProtocolEvent> key = new EventKey<>() {
        @Getter
        private final int version = 0;

        @Override
        public @NonNull Class<ProtocolEvent> getEventClass() {
            return ProtocolEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "ProtocolEvent";
        }

        @Override
        public void encode(@NonNull ProtocolEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeInt(EventManager.hash());
        }

        @Override
        public @NonNull ProtocolEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            return new ProtocolEvent(stream.readInt());
        }
    };
}
