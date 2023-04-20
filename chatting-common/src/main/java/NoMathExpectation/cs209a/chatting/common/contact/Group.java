package NoMathExpectation.cs209a.chatting.common.contact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class Group extends Contact {
    @Getter
    protected List<User> members = new CopyOnWriteArrayList<>();

    public Group(@NonNull UUID id, @NonNull String name) {
        super(id, name);
    }
}
