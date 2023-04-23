package NoMathExpectation.cs209a.chatting.common.contact;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class User extends Contact {
    public User(@NonNull UUID id, @NonNull String name) {
        super(id, name);
    }
}
