package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import NoMathExpectation.cs209a.chatting.common.event.meta.Event;
import NoMathExpectation.cs209a.chatting.common.event.meta.EventKey;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Value
public class ContactsEvent implements Event {
    Map<UUID, Contact> contacts;

    public static final EventKey<ContactsEvent> key = new EventKey<>() {
        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public @NonNull Class<ContactsEvent> getEventClass() {
            return ContactsEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "ContactListEvent";
        }

        @Override
        public void encode(@NonNull ContactsEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            val contacts = event.getContacts();
            stream.writeInt(contacts.size());
            contacts.forEach((uuid, contact) -> {
                try {
                    stream.writeUTF(uuid.toString());
                    stream.writeUTF(contact.getName());
                    if (contact instanceof User) {
                        stream.writeUTF("User");
                        return;
                    }
                    if (contact instanceof Group) {
                        stream.writeUTF("Group");
                        return;
                    }
                    throw new IllegalArgumentException("Unknown contact type: " + contact.getClass().getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public @NonNull ContactsEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            val count = stream.readInt();
            val contacts = new HashMap<UUID, Contact>(count);
            for (int i = 0; i < count; i++) {
                val uuid = UUID.fromString(stream.readUTF());
                val name = stream.readUTF();
                val type = stream.readUTF();

                switch (type) {
                    case "User":
                        contacts.put(uuid, Connector.getInstance().newUser(uuid, name));
                        break;
                    case "Group":
                        contacts.put(uuid, Connector.getInstance().newGroup(uuid, name));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown contact type: " + type);
                }
            }

            return new ContactsEvent(contacts);
        }
    };
}
