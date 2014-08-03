package at.yawk.mcomponent.style;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yawkat
 */
public class StyleOperations {
    private StyleOperations() {}

    /**
     * Get a style that contains any elements in old that are not in remove.
     */
    public static Style removeMatching(Style old, Style remove) {
        Color color = old.color == remove.color ? Color.INHERIT : old.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        flags.putAll(old.flags);
        for (Iterator<Map.Entry<FlagKey, FlagValue>> iterator = flags.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<FlagKey, FlagValue> entry = iterator.next();
            if (remove.getFlag(entry.getKey()) == entry.getValue()) {
                iterator.remove();
            }
        }
        return Style.create(Collections.unmodifiableMap(flags), color);
    }

    /**
     * Get a style that contains the child's styles with inherited attributes replaced by the parent's.
     */
    public static Style inherit(Style child, Style parent) {
        Color color = child.color == Color.INHERIT ? parent.color : child.color;
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        flags.putAll(parent.flags);
        child.flags.forEach((k, v) -> v.getValue().ifPresent(b -> flags.put(k, v)));
        return Style.create(Collections.unmodifiableMap(flags), color);
    }

    /**
     * Get a style with all attributes in parent that are overridden by the child.
     */
    public static Style overridden(Style child, Style parent) {
        Map<FlagKey, FlagValue> flags = Maps.newEnumMap(FlagKey.class);
        for (FlagKey key : FlagKey.values()) {
            FlagValue flag = parent.getFlag(key);
            if (flag != FlagValue.INHERIT && flag == child.getFlag(key)) {
                flags.put(key, flag);
            }
        }
        Color color = child.color == Color.INHERIT ? Color.INHERIT : parent.color;
        return Style.create(Collections.unmodifiableMap(flags), color);
    }
}
