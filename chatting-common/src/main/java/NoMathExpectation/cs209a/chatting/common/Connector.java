package NoMathExpectation.cs209a.chatting.common;

import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import lombok.Getter;
import lombok.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public abstract class Connector implements Runnable, Closeable {
    @Getter
    protected static Connector instance;

    protected ObjectInputStream incoming = null;
    protected ObjectOutputStream outgoing = null;

    @Getter
    protected Map<UUID, Contact> contacts = new HashMap<>();

    public abstract void sendEvent(@NonNull Event event) throws IOException;

    @Getter
    protected boolean connected = false;
}
