package NoMathExpectation.cs209a.chatting.client.event.meta;

import NoMathExpectation.cs209a.chatting.client.contact.UserImpl;
import NoMathExpectation.cs209a.chatting.client.gui.Chat;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.event.UserLoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLogoutEvent;
import javafx.application.Platform;
import lombok.NonNull;
import lombok.val;

public class EventHandlers {
    public static void registerAllHandlers() {
        UserLoginEvent.key.registerEvent(EventHandlers::handleLoginEvent);
        UserLogoutEvent.key.registerEvent(EventHandlers::handleLogoutEvent);
    }

    public static void handleLoginEvent(@NonNull UserLoginEvent event) {
        val user = new UserImpl(event.getId(), event.getName());

        Connector.getInstance().getContacts().put(user.getId(), user);
        Platform.runLater(() -> Chat.addContact(user));
    }

    public static void handleLogoutEvent(@NonNull UserLogoutEvent event) {
        Connector.getInstance().getContacts().remove(event.getId());
        Platform.runLater(() -> Chat.removeContact(event.getId()));
    }
}
