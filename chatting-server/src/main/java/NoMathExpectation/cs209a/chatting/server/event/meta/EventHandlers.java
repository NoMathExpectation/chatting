package NoMathExpectation.cs209a.chatting.server.event.meta;

import NoMathExpectation.cs209a.chatting.common.event.UserLoginEvent;
import NoMathExpectation.cs209a.chatting.common.event.UserLogoutEvent;
import NoMathExpectation.cs209a.chatting.server.ServerConnectorImpl;
import lombok.NonNull;

public class EventHandlers {
    public static void registerAllHandlers() {
        UserLoginEvent.key.registerEvent(EventHandlers::handleLoginEvent);
        UserLogoutEvent.key.registerEvent(EventHandlers::handleLogoutEvent);
    }

    public static void handleLoginEvent(@NonNull UserLoginEvent event) {
        ServerConnectorImpl.sendEvent(event, x -> true);
    }

    public static void handleLogoutEvent(@NonNull UserLogoutEvent event) {
        ServerConnectorImpl.sendEvent(event, x -> true);
    }
}
