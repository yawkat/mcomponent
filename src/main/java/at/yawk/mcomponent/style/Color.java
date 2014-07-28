package at.yawk.mcomponent.style;

import java.util.Optional;

/**
 * @author yawkat
 */
public enum Color {
    BLACK("black"),
    DARK_BLUE("dark_blue"),
    DARK_GREEN("dark_green"),
    DARK_AQUA("dark_aqua"),
    DARK_RED("dark_red"),
    DARK_PURPLE("dark_purple"),
    GOLD("gold"),
    GRAY("gray"),
    DARK_GRAY("dark_gray"),
    BLUE("blue"),
    GREEN("green"),
    AQUA("aqua"),
    RED("red"),
    LIGHT_PURPLE("light_purple"),
    YELLOW("yellow"),
    WHITE("white"),

    OBFUSCATED("obfuscated", FlagKey.OBFUSCATED),
    BOLD("bold", FlagKey.BOLD),
    STRIKETHROUGH("strikethrough", FlagKey.STRIKETHROUGH),
    UNDERLINE("underline", FlagKey.UNDERLINE),
    ITALIC("italic", FlagKey.ITALIC),

    RESET("reset"),

    INHERIT;

    private final Optional<String> name;
    private final Optional<FlagKey> flag;

    Color(String name, FlagKey flag) {
        this.name = Optional.of(name);
        this.flag = Optional.of(flag);
    }

    Color(String name) {
        this.name = Optional.of(name);
        this.flag = Optional.empty();
    }

    Color() {
        this.name = Optional.empty();
        this.flag = Optional.empty();
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<FlagKey> getFlag() {
        return flag;
    }
}
