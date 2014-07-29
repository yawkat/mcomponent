package at.yawk.mcomponent.style;

import at.yawk.mcomponent.JsonSerializable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author yawkat
 */
public class Style implements JsonSerializable {
    public static final Style DEFAULT;
    public static final Style INHERIT = new Style(Color.INHERIT);

    // mutable for internal building
    private Map<FlagKey, FlagValue> flags;
    private Color color;

    static {
        Map<FlagKey, FlagValue> flags = new EnumMap<>(FlagKey.class);
        Arrays.stream(FlagKey.values()).forEach(k -> flags.put(k, FlagValue.INHERIT));
        DEFAULT = new Style(flags, Color.WHITE);
    }

    Style() {}

    /**
     * Internal constructor that assumes the given flag map is immutable.
     */
    static Style create(Map<FlagKey, FlagValue> immutableFlags, Color color) {
        Style style = new Style();
        style.flags = immutableFlags;
        style.color = color;
        return style;
    }

    public Style(Map<FlagKey, FlagValue> flags, Color color) {
        this.flags = ImmutableMap.copyOf(flags);
        this.color = color;
    }

    public Style(Color color) {
        this.flags = Collections.emptyMap();
        this.color = color;
    }

    public boolean hasChangesFromParent(Style parent) {
        if (color != Color.INHERIT && color != parent.color) {
            return true;
        }
        for (FlagKey key : FlagKey.values()) {
            FlagValue ours = getFlag(key);
            if (ours != FlagValue.INHERIT) {
                FlagValue theirs = parent.getFlag(key);
                if (ours != theirs) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return a style that has all properties the parent shares with this style removed.
     */
    public Style subtractParent(Style parent) {
        Color color = this.color == parent.color ? Color.INHERIT : this.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        flags.putAll(this.flags);
        for (Iterator<Map.Entry<FlagKey, FlagValue>> iterator = flags.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<FlagKey, FlagValue> entry = iterator.next();
            if (parent.getFlag(entry.getKey()) == entry.getValue()) {
                iterator.remove();
            }
        }
        return create(Collections.unmodifiableMap(flags), color);
    }

    /**
     * Return a style that has all the inherited properties replaced by the parents properties.
     */
    public Style addParent(Style parent) {
        Color color = this.color == Color.INHERIT ? parent.color : this.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        flags.putAll(parent.flags);
        this.flags.forEach((k, v) -> v.getValue().ifPresent(b -> flags.put(k, v)));
        return create(Collections.unmodifiableMap(flags), color);
    }

    public Style getOverridden(Style other) {
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        for (FlagKey key : FlagKey.values()) {
            FlagValue flag = getFlag(key);
            if (flag != FlagValue.INHERIT && flag == other.getFlag(key)) {
                flags.put(key, flag);
            }
        }
        Color color = other.color == Color.INHERIT ? Color.INHERIT : this.color;
        return create(Collections.unmodifiableMap(flags), color);
    }

    public FlagValue getFlag(FlagKey key) {
        return flags.getOrDefault(key, FlagValue.INHERIT);
    }

    public Style withFlag(FlagKey key, FlagValue value) {
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        flags.putAll(this.flags);
        if (value == FlagValue.INHERIT) {
            flags.remove(key);
        } else {
            flags.put(key, value);
        }
        return create(Collections.unmodifiableMap(flags), color);
    }

    public Style withColor(Color color) {
        return create(this.flags, color);
    }

    public void applyToJson(JsonObject object) {
        flags.forEach((k, v) -> v.getValue().ifPresent(b -> object.addProperty(k.getKey(), b)));
        color.getName().ifPresent(name -> object.addProperty("color", name));
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        //noinspection Convert2streamapi
        for (Map.Entry<FlagKey, FlagValue> entry : flags.entrySet()) {
            if (entry.getValue().getValue().isPresent()) {
                writer.name(entry.getKey().getKey());
                writer.value(entry.getValue().getValue().get());
            }
        }
        if (color.getName().isPresent()) {
            writer.name("color");
            writer.value(color.getName().get());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Style style = (Style) o;

        if (color != style.color) {
            return false;
        }
        if (!flags.equals(style.flags)) {
            return false;
        }
        for (FlagKey key : FlagKey.values()) {
            if (getFlag(key) != style.getFlag(key)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (FlagKey key : FlagKey.values()) {
            result = 31 * result + getFlag(key).hashCode();
        }
        result = 31 * result + color.hashCode();
        return result;
    }
}
