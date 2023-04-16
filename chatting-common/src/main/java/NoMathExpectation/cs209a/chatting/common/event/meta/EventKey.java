package NoMathExpectation.cs209a.chatting.common.event.meta;

import lombok.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface EventKey<E extends Event> {
    @NonNull int getVersion();

    @NonNull Class<E> getEventClass();

    @NonNull String getId();

    void encode(@NonNull E Event, @NonNull ObjectOutputStream stream) throws IOException;

    @NonNull E decode(@NonNull ObjectInputStream stream) throws IOException;
}
