package at.yawk.mcomponent;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author yawkat
 */
public class TranslatableComponentValue implements ComponentValue {
    private final String key;
    private final List<Component> arguments;

    public TranslatableComponentValue(String key, List<Component> arguments) {
        this.key = key;
        this.arguments = ImmutableList.copyOf(arguments);
    }

    public String getKey() {
        return key;
    }

    public List<Component> getArguments() {
        return arguments;
    }

    @Override
    public void applyToJson(JsonObject target) {
        target.addProperty("translate", getKey());
        if (!getArguments().isEmpty()) {
            JsonArray argArray = new JsonArray();
            getArguments().forEach(arg -> argArray.add(arg.toJson()));
            target.add("with", argArray);
        }
    }

    @Override
    public String toRawString() {
        return "{" + key + ">" + arguments + "}";
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.name("translate");
        writer.value(getKey());
        if (!getArguments().isEmpty()) {
            writer.beginArray();
            for (Component component : arguments) {
                component.write(writer);
            }
            writer.endArray();
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

        TranslatableComponentValue that = (TranslatableComponentValue) o;

        if (!arguments.equals(that.arguments)) {
            return false;
        }
        if (!key.equals(that.key)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
