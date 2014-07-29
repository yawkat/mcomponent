package at.yawk.mcomponent;

import at.yawk.mcomponent.style.Style;
import com.google.gson.JsonElement;
import java.util.Optional;

/**
 * @author yawkat
 */
public interface Component extends JsonSerializable {
    JsonElement toJson();

    Component minify();

    boolean isEmpty();

    Optional<Component> tryJoin(Component other);

    Component withStyle(Style style);

    String toRawString();
}
