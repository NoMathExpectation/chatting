package NoMathExpectation.cs209a.chatting.common;

import lombok.NonNull;
import lombok.Value;

@Value
public class Message {
    @NonNull Long timestamp;
    @NonNull String sentBy;
    @NonNull String sendTo;
    @NonNull String text;
}
