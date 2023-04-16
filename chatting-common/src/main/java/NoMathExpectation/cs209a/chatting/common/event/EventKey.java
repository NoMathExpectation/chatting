package NoMathExpectation.cs209a.chatting.common.event;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EventKey<E extends Event> {
    @NonNull int getVersion();

    @NonNull Class<E> getEventClass();

    @NonNull String getId();

    void encode(@NonNull E Event, @NonNull OutputStream stream) throws IOException;

    @NonNull E decode(@NonNull InputStream stream) throws IOException;
}
