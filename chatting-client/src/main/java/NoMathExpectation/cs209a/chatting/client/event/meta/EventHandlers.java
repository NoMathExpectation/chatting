package NoMathExpectation.cs209a.chatting.client.event.meta;

import NoMathExpectation.cs209a.chatting.client.contact.UserImpl;
import NoMathExpectation.cs209a.chatting.client.gui.Chat;
import NoMathExpectation.cs209a.chatting.client.gui.ChatContact;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLogoutEvent;
import javafx.application.Platform;
import lombok.NonNull;
import lombok.val;

public class EventHandlers {
    public static void registerAllHandlers() {
        UserLoginEvent.key.registerEvent(EventHandlers::handleLoginEvent);
        UserLogoutEvent.key.registerEvent(EventHandlers::handleLogoutEvent);
        MessageEvent.key.registerEvent(EventHandlers::handleMessageEvent);
    }

    public static void handleLoginEvent(@NonNull UserLoginEvent event) {
        val user = new UserImpl(event.getId(), event.getName());

        Connector.getInstance().getContacts().put(user.getId(), user);
        Platform.runLater(() -> Chat.addContact(user));
    }

    public static void handleLogoutEvent(@NonNull UserLogoutEvent event) {
        Platform.runLater(() -> {
            ChatContact.of(event.getId()).setOffline();
            Chat.removeContact(event.getId());
            Connector.getInstance().getContacts().remove(event.getId());
        });
    }

    public static void handleMessageEvent(@NonNull MessageEvent event) {
        Contact contact;
        if (event.getSendTo() instanceof Group) {
            contact = event.getSendTo();
        } else {
            contact = event.getSentBy();
        }

        Platform.runLater(() -> {
            val chat = ChatContact.of(contact);
            chat.addMessage(event);
            chat.getStage().show();
        });
    }
}
