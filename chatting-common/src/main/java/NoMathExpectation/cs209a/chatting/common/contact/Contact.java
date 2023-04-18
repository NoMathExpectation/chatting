package NoMathExpectation.cs209a.chatting.common.contact;

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

    public abstract void sendMessage(String message);
}
