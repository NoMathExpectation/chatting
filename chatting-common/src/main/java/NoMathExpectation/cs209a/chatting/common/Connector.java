package NoMathExpectation.cs209a.chatting.common;

import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.Event;
import lombok.Getter;
import lombok.NonNull;

import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Connector implements Runnable, Closeable {
    @Getter
    protected static Connector instance;

    protected Map<UUID, Contact> contacts = new HashMap<>();

    public Map<UUID, Contact> getContacts() {
        return Collections.unmodifiableMap(contacts);
    }

    public abstract void sendEvent(@NonNull Event event);

    @Getter
    protected boolean connected = false;
}
