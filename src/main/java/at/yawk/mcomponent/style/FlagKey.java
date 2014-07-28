package at.yawk.mcomponent.style;

/**
 * @author yawkat
 */
public enum FlagKey {
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINE("underline"),
    STRIKETHROUGH("strikethrough"),
    OBFUSCATED("obfuscated"),
    ;

    private final String key;

    FlagKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
