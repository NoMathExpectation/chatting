package NoMathExpectation.cs209a.chatting.common.contact;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Group extends Contact {
    public Group(@NonNull UUID id, @NonNull String name) {
        super(id, name);
    }

    @Override
    public void sendMessage(String message) {
        throw new UnsupportedOperationException("This is a data only instance.");
    }
}
