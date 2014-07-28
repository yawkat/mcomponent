package at.yawk.mcomponent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * @author yawkat
 */
public class StringComponent implements Component {
    private final String value;

    public StringComponent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public Component minify() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public Optional<Component> tryJoin(Component other) {
        if (other instanceof StringComponent) {
            return Optional.of(new StringComponent(this.value + ((StringComponent) other).value));
        }
        return Optional.empty();
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.value(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringComponent that = (StringComponent) o;

        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
