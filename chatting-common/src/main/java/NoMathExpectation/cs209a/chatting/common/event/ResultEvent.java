package NoMathExpectation.cs209a.chatting.common.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class ResultEvent implements Event {
    public final int result;
    public final @NonNull String reason;

    public static final EventKey<ResultEvent> key = new EventKey<>() {
        @Override
        public Class<ResultEvent> getEventClass() {
            return ResultEvent.class;
        }

        @Override
        public String getId() {
            return "ResultEvent";
        }

        @Override
        public void encode(ResultEvent event, OutputStream stream) throws IOException {
            ObjectOutputStream outputStream = new ObjectOutputStream(stream);
            outputStream.writeInt(event.result);
            outputStream.writeUTF(event.reason);
        }

        @Override
        public ResultEvent decode(InputStream stream) throws IOException {
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            return new ResultEvent(inputStream.readInt(), inputStream.readUTF());
        }
    };
}
