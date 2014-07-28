package at.yawk.mcomponent.action;

import at.yawk.mcomponent.JsonSerializable;
import com.google.gson.JsonObject;

/**
 * @author yawkat
 */
public interface Event extends JsonSerializable {
    void applyToJson(JsonObject target);
}
