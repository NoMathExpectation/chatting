package NoMathExpectation.cs209a.chatting.common.event.meta;

import NoMathExpectation.cs209a.chatting.common.event.*;
import lombok.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class EventManager {
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

    public static int hash() {
        return Arrays.hashCode(keyMap.values().stream().mapToInt(EventKey::getVersion).toArray());
    }

    static {
        registerEvent(ContactsEvent.key);
        registerEvent(GroupCreateEvent.key);
        registerEvent(GroupInviteEvent.key);
        registerEvent(LoginEvent.key);
        registerEvent(MessageEvent.key);
        registerEvent(ProtocolEvent.key);
        registerEvent(ResultEvent.key);
        registerEvent(UserLoginEvent.key);
        registerEvent(UserLogoutEvent.key);
    }
}
