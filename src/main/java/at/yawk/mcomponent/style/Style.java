/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent.style;

import at.yawk.mcomponent.JsonSerializable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * @author yawkat
 */
@EqualsAndHashCode
public class Style implements JsonSerializable {
    public static final Style DEFAULT;
    public static final Style INHERIT = new Style(Color.INHERIT);

    // mutable for internal building
    Map<FlagKey, FlagValue> flags;
    Color color;

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

    public Color getColor() {
        return color;
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
    public String toString() {
        JsonObject jo = new JsonObject();
        applyToJson(jo);
        return "Style" + jo;
    }
}
