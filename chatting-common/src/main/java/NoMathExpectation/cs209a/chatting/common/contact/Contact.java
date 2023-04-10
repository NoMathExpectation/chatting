package NoMathExpectation.cs209a.chatting.common.contact;

import lombok.*;

import java.util.UUID;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Contact {
    @EqualsAndHashCode.Include
    @Getter
    private final UUID id = UUID.randomUUID();

    @Getter
    @Setter
    private @NonNull String name;

    public abstract void sendMessage(String message);
}
