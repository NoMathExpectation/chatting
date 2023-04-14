package NoMathExpectation.cs209a.chatting.common.event;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static final Map<@NonNull String, @NonNull EventKey<? extends Event>> keyMap = new HashMap<>();

    private static final Map<Class<? extends Event>, EventKey<? extends Event>> classEventKeyMap = new HashMap<>();

    public static void registerEvent(@NonNull EventKey<? extends Event> eventKey) {
        classEventKeyMap.put(eventKey.getEventClass(), eventKey);
        keyMap.put(eventKey.getId(), eventKey);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> EventKey<E> keyOf(@NonNull Class<E> eventClass) {
        return (EventKey<E>) classEventKeyMap.get(eventClass);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> EventKey<E> keyOf(@NonNull E event) {
        return (EventKey<E>) keyOf(event.getClass());
    }

    public static EventKey<? extends Event> keyOf(@NonNull String id) {
        return keyMap.get(id);
    }

    static {
        registerEvent(MessageEvent.getKey());
    }
}
