package at.yawk.mcomponent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public class StringComponentValue implements ComponentValue {
    private final String value;

    public StringComponentValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void applyToJson(JsonObject target) {
        target.addProperty("text", getValue());
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.name("text");
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

        StringComponentValue that = (StringComponentValue) o;

        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
