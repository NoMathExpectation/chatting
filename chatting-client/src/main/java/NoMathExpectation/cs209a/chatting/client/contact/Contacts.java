package NoMathExpectation.cs209a.chatting.client.contact;

import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.User;
import lombok.NonNull;

public class Contacts {
    public static @NonNull Contact of(@NonNull Contact contact) {
        if (contact instanceof User) {
            return new UserImpl((User) contact);
        }

        throw new IllegalArgumentException("Unknown contact type: " + contact.getClass().getName());
    }
}
