package NoMathExpectation.cs209a.chatting.common.event;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.InputStream;

public interface EventKey {
    @NonNull String getId();

    @SneakyThrows
    @NonNull Event decode(@NonNull InputStream stream);
}
