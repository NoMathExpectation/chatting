package NoMathExpectation.cs209a.chatting.common.contact;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@RequiredArgsConstructor
public abstract class Group extends Contact {
}
