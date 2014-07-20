package at.yawk.mcomponent.style;

import java.util.Optional;

/**
* @author yawkat
*/
public enum FlagValue {
    TRUE(Optional.of(true)),
    FALSE(Optional.of(false)),
    INHERIT(Optional.<Boolean>empty());

    private final Optional<Boolean> value;

    FlagValue(Optional<Boolean> value) {
        this.value = value;
    }

    public Optional<Boolean> getValue() {
        return value;
    }
}
