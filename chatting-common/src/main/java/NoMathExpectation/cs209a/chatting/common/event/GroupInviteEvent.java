package NoMathExpectation.cs209a.chatting.common.event;

import NoMathExpectation.cs209a.chatting.common.Connector;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Value
public class GroupInviteEvent implements Event {
    @NonNull Group group;
    @NonNull List<User> members;

    public static final EventKey<GroupInviteEvent> key = new EventKey<>() {
        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public @NonNull Class<GroupInviteEvent> getEventClass() {
            return GroupInviteEvent.class;
        }

        @Override
        public @NonNull String getId() {
            return "GroupInviteEvent";
        }

        @Override
        public void encode(@NonNull GroupInviteEvent event, @NonNull ObjectOutputStream stream) throws IOException {
            stream.writeUTF(event.group.getId().toString());
            stream.writeUTF(event.group.getName());
            stream.writeUTF(event.group.getOwner().getId().toString());
            stream.writeInt(event.members.size());
            for (val member : event.members) {
                stream.writeUTF(member.getId().toString());
            }
        }

        @Override
        public @NonNull GroupInviteEvent decode(@NonNull ObjectInputStream stream) throws IOException {
            val connector = Connector.getInstance();

            val id = UUID.fromString(stream.readUTF());
            val name = stream.readUTF();
            val owner = connector.getContacts().get(UUID.fromString(stream.readUTF()));
            var group = connector.getContacts().get(id);
            if (group == null) {
                if (!(owner instanceof User)) {
                    throw new IllegalArgumentException("Owner is not a user.");
                }
                group = connector.newGroup(id, name, (User) owner);
            } else if (!(group instanceof Group)) {
                throw new IllegalArgumentException("Group is not a group.");
            }

            val count = stream.readInt();
            List<User> members = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                val member = connector.getContacts().get(UUID.fromString(stream.readUTF()));
                if (!(member instanceof User)) {
                    continue;
                }
                members.add((User) member);
            }
            return new GroupInviteEvent((Group) group, members);
        }
    };
}
