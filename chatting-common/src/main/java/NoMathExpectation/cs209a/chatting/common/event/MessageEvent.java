package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import lombok.*;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.UUID;

@Value
public class MessageEvent implements Event {
    @NonNull Instant time;
    @NonNull User sentBy;
    @NonNull Contact sendTo;
    @NonNull String message;

    @Getter
    private static EventKey key = new EventKey() {
        @Override
        public @NonNull String getId() {
            return "MessageEvent";
        }

        @Override
        @SneakyThrows
        public @NonNull MessageEvent decode(@NonNull InputStream stream) {
            val inputStream = new ObjectInputStream(stream);
            val time = Instant.parse(inputStream.readUTF());
            val sentBy = Connector.getInstance().getContacts().get(UUID.fromString(inputStream.readUTF()));
            val sendTo = Connector.getInstance().getContacts().get(UUID.fromString(inputStream.readUTF()));
            val message = inputStream.readUTF();

            if (!(sentBy instanceof User && sendTo != null)) {
                throw new IllegalArgumentException("Invalid MessageEvent received.");
            }
            return new MessageEvent(time, (User) sentBy, sendTo, message);
        }
    };

    @Override
    @SneakyThrows
    public void encode(@NonNull OutputStream stream) {
        val outputStream = new ObjectOutputStream(stream);
        outputStream.writeUTF(time.toString());
        outputStream.writeUTF(sentBy.getId().toString());
        outputStream.writeUTF(sendTo.getId().toString());
        outputStream.writeUTF(message);
    }
}
