package NoMathExpectation.cs209a.chatting.common.event;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;

public interface Event {
    @NonNull EventKey getKey();

    default @NonNull String getId() {
        return getKey().getId();
    }

    @SneakyThrows
    void encode(@NonNull OutputStream stream);

    default @NonNull Event decode(@NonNull InputStream stream) {
        return getKey().decode(stream);
    }
}
