package NoMathExpectation.cs209a.chatting.client.event.meta;

import NoMathExpectation.cs209a.chatting.client.ConnectorImpl;
import NoMathExpectation.cs209a.chatting.client.contact.UserImpl;
import NoMathExpectation.cs209a.chatting.client.gui.Chat;
import NoMathExpectation.cs209a.chatting.client.gui.ChatContact;
import NoMathExpectation.cs209a.chatting.common.Connector;
import NoMathExpectation.cs209a.chatting.common.contact.Contact;
import NoMathExpectation.cs209a.chatting.common.contact.Group;
import NoMathExpectation.cs209a.chatting.common.event.GroupInviteEvent;
import NoMathExpectation.cs209a.chatting.common.event.MessageEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLogoutEvent;
import javafx.application.Platform;
import lombok.NonNull;
import lombok.val;

import java.util.Objects;

public class EventHandlers {
    private EventHandlers() {
        throw new UnsupportedOperationException();
    }

    public static void registerAllHandlers() {
        UserLoginEvent.key.registerEvent(EventHandlers::handleLoginEvent);
        UserLogoutEvent.key.registerEvent(EventHandlers::handleLogoutEvent);
        MessageEvent.key.registerEvent(EventHandlers::handleMessageEvent);
        GroupInviteEvent.key.registerEvent(EventHandlers::handleGroupInviteEvent);
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
            Connector.getInstance().getContacts().forEach((uuid, contact) -> {
                if (contact instanceof Group) {
                    ((Group) contact).getMembers().removeIf(member -> Objects.equals(member.getId(), event.getId()));
                }
            });
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

    public static void handleGroupInviteEvent(@NonNull GroupInviteEvent event) {
        var group = event.getGroup();
        val members = event.getMembers();

        Connector.getInstance().getContacts().putIfAbsent(group.getId(), group);
        group = (Group) Connector.getInstance().getContacts().get(group.getId());
        group.getMembers().addAll(event.getMembers());

        val finalGroup = group;
        Platform.runLater(() -> {
            val chatContact = ChatContact.of(finalGroup);
            chatContact.addMembers(members);

            if (members.contains(ConnectorImpl.getClientInstance().getUser())) {
                Chat.addContact(finalGroup);
                chatContact.getStage().show();
            }
        });
    }
}
