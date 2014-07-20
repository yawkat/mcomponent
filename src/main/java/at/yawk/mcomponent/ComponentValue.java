package at.yawk.mcomponent;

import com.google.gson.JsonObject;

/**
 * @author yawkat
 */
public interface ComponentValue extends JsonSerializable {
    public static ComponentValue EMPTY = new StringComponent("");

    void applyToJson(JsonObject target);
}
