package NoMathExpectation.cs209a.chatting.server.event.meta;

import NoMathExpectation.cs209a.chatting.common.event.*;
import NoMathExpectation.cs209a.chatting.server.ServerConnectorImpl;
import NoMathExpectation.cs209a.chatting.server.contact.UserImpl;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Objects;
import java.util.UUID;

public class EventHandlers {
    private EventHandlers() {
        throw new UnsupportedOperationException();
    }

    public static void registerAllHandlers() {
        UserLoginEvent.key.registerEvent(EventHandlers::handleLoginEvent);
        UserLogoutEvent.key.registerEvent(EventHandlers::handleLogoutEvent);
        MessageEvent.key.registerEvent(EventHandlers::handleMessageEvent);
        GroupCreateEvent.key.registerEvent(EventHandlers::handleGroupCreateEvent);
        GroupInviteEvent.key.registerEvent(EventHandlers::handleGroupInviteEvent);
    }

    public static void handleLoginEvent(@NonNull UserLoginEvent event) {
        ServerConnectorImpl.sendEvent(event, x -> true);
    }

    public static void handleLogoutEvent(@NonNull UserLogoutEvent event) {
        ServerConnectorImpl.sendEvent(event, x -> true);
    }

    public static void handleMessageEvent(@NonNull MessageEvent event) {
        if (!Objects.equals(event.getSentBy(), event.getSendTo())) {
            event.getSendTo().sendMessage(event);
        }
    }

    @SneakyThrows
    public static void handleGroupCreateEvent(@NonNull GroupCreateEvent event) {
        val connector = ServerConnectorImpl.getInstance();
        val owner = event.getOwner();
        val group = connector.newGroup(UUID.randomUUID(), event.getName(), owner);

        connector.getContacts().put(group.getId(), group);
        ((UserImpl) owner).getClient().sendEvent(new GroupInviteEvent(group, group.getMembers()));
    }

    @SneakyThrows
    public static void handleGroupInviteEvent(@NonNull GroupInviteEvent event) {
        val group = event.getGroup();
        val members = event.getMembers();

        for (val member : group.getMembers()) {
            ((UserImpl) member).getClient().sendEvent(event);
        }

        group.getMembers().addAll(members);

        val newEvent = new GroupInviteEvent(group, group.getMembers());
        for (val member : members) {
            ((UserImpl) member).getClient().sendEvent(newEvent);
        }
    }
}
