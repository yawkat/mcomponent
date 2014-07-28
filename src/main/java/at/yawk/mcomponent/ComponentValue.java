package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import java.util.Optional;

/**
 * @author yawkat
 */
public interface ComponentValue extends JsonSerializable {
    public static ComponentValue EMPTY = new StringComponentValue("");

    void applyToJson(JsonObject target);

    default boolean isEmpty() {
        return false;
    }

    default Optional<ComponentValue> tryJoin(ComponentValue other) {
        return Optional.empty();
    }
}
