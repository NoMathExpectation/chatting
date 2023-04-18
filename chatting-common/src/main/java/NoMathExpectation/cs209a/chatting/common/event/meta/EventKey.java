package NoMathExpectation.cs209a.chatting.common.event.meta;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j(topic = "EventKey")
public abstract class EventKey<E extends Event> {
    private final List<EventHandler<? super E>> handlers = new ArrayList<>();

    public List<EventHandler<? super E>> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    public void registerEvent(EventHandler<? super E> handler) {
        handlers.add(handler);
    }

    public void broadcast(E event) {
        log.info("Broadcasting event: {}", event);
        for (EventHandler<? super E> handler : handlers) {
            try {
                handler.handle(event);
            } catch (Exception e) {
                log.error("Exception thrown while handling event: " + event, e);
            }
        }
    }

    public abstract int getVersion();

    public abstract @NonNull Class<E> getEventClass();

    public abstract @NonNull String getId();

    public abstract void encode(@NonNull E event, @NonNull ObjectOutputStream stream) throws IOException;

    public abstract @NonNull E decode(@NonNull ObjectInputStream stream) throws IOException;

    public void decodeAndBroadcast(@NonNull ObjectInputStream stream) throws IOException {
        broadcast(decode(stream));
    }
}
