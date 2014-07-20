package at.yawk.mcomponent;

import com.google.gson.JsonElement;

/**
 * @author yawkat
 */
public interface Component extends JsonSerializable {
    JsonElement toJson();

    Component minify();
}
