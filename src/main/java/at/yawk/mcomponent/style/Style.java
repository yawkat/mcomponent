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

    private final Map<FlagKey, FlagValue> flags;
    private final Color color;

    static {
        Map<FlagKey, FlagValue> flags = new EnumMap<>(FlagKey.class);
        Arrays.stream(FlagKey.values()).forEach(k -> flags.put(k, FlagValue.INHERIT));
        DEFAULT = new Style(flags, Color.WHITE);
    }

    public Style(Map<FlagKey, FlagValue> flags, Color color) {
        this.flags = ImmutableMap.copyOf(flags);
        this.color = color;
    }

    public Style(Color color) {
        this(Collections.emptyMap(), color);
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

    public Style subtractParent(Style parent) {
        Color color = this.color == parent.color ? Color.INHERIT : this.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(this.flags);
        for (Iterator<Map.Entry<FlagKey, FlagValue>> iterator = flags.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<FlagKey, FlagValue> entry = iterator.next();
            if (parent.getFlag(entry.getKey()) == entry.getValue()) {
                iterator.remove();
            }
        }
        return new Style(flags, color);
    }

    public Style addParent(Style parent) {
        Color color = this.color == Color.INHERIT ? parent.color : this.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(parent.flags);
        this.flags.forEach((k, v) -> v.getValue().ifPresent(b -> flags.put(k, v)));
        return new Style(flags, color);
    }

    public FlagValue getFlag(FlagKey key) {
        return flags.getOrDefault(key, FlagValue.INHERIT);
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
