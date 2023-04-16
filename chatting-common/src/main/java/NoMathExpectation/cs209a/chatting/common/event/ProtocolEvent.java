package NoMathExpectation.cs209a.chatting.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class ProtocolEvent implements Event {
    public static final EventKey<ProtocolEvent> key = new EventKey<>() {
        @Getter
        private final int version = 0;

        @Override
        public Class<ProtocolEvent> getEventClass() {
            return ProtocolEvent.class;
        }

        @Override
        public String getId() {
            return "ProtocolEvent";
        }

        @Override
        public void encode(ProtocolEvent event, OutputStream stream) throws IOException {
            ObjectOutputStream outputStream = new ObjectOutputStream(stream);
            outputStream.writeInt(EventManager.hash());
        }

        @Override
        public ProtocolEvent decode(InputStream stream) throws IOException {
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            return new ProtocolEvent(inputStream.readInt());
        }
    };

    public final int hash;
}
