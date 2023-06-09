package NoMathExpectation.cs209a.chatting.common.contact;

import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import lombok.*;

import java.util.UUID;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Contact {
    @EqualsAndHashCode.Include
    @Getter
    private final UUID id;

    @Getter
    @Setter
    private @NonNull String name;

    public Contact(@NonNull String name) {
        this(UUID.randomUUID(), name);
    }

    public abstract void sendMessage(MessageEvent messageEvent);

    public abstract @NonNull MessageEvent sendMessage(String message);
}
