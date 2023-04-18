package NoMathExpectation.cs209a.chatting.common.event.meta;

import lombok.NonNull;

@FunctionalInterface
public interface EventHandler<E extends Event> {
    void handle(@NonNull E event);
}
