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
public class ResultEvent implements Event {
    int result;
    @NonNull String reason;

    public static final EventKey<ResultEvent> key = new EventKey<>() {
        @Getter
        private final int version = 0;

        @Override
        public @NonNull Class<ResultEvent> getEventClass() {
            return ResultEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "ResultEvent";
        }

        @Override
        public void encode(@NonNull ResultEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeInt(event.result);
            stream.writeUTF(event.reason);
        }

        @Override
        public @NonNull ResultEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            return new ResultEvent(stream.readInt(), stream.readUTF());
        }
    };
}
