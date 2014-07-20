package at.yawk.mcomponent;

import at.yawk.mcomponent.style.Style;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author yawkat
 */
public class BaseComponent implements Component {
    private final ComponentValue value;
    private final List<Component> children;
    private final Style style;

    public BaseComponent(ComponentValue value, List<Component> children, Style style) {
        this.value = value;
        this.children = ImmutableList.copyOf(children);
        this.style = style;
    }

    public BaseComponent(ComponentValue value, List<Component> children) {
        this(value, children, Style.INHERIT);
    }

    public BaseComponent(ComponentValue value) {
        this(value, Collections.emptyList());
    }

    public ComponentValue getValue() {
        return value;
    }

    public List<Component> getChildren() {
        return children;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        value.applyToJson(object);
        if (!children.isEmpty()) {
            JsonArray childArray = new JsonArray();
            children.forEach(child -> childArray.add(child.toJson()));
            object.add("extra", childArray);
        }
        style.applyToJson(object);
        return object;
    }

    @Override
    public Component minify() {
        if (children.isEmpty()
                && value instanceof StringComponentValue
                && style.equals(Style.INHERIT)) {
            return new StringComponent(((StringComponentValue) value).getValue());
        }
        return this;
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        value.write(writer);
        if (!children.isEmpty()) {
            writer.name("extra");
            writer.beginArray();
            for (Component child : children) {
                child.write(writer);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseComponent that = (BaseComponent) o;

        if (!children.equals(that.children)) {
            return false;
        }
        if (!style.equals(that.style)) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + children.hashCode();
        result = 31 * result + style.hashCode();
        return result;
    }
}
