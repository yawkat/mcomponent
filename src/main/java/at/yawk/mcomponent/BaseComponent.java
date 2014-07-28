package at.yawk.mcomponent;

import at.yawk.mcomponent.action.Event;
import at.yawk.mcomponent.style.Style;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yawkat
 */
public class BaseComponent implements Component {
    private final ComponentValue value;
    private final List<Component> children;
    private final Style style;
    private final Set<Event> events;

    public BaseComponent(ComponentValue value, List<Component> children, Style style, Set<Event> events) {
        this.value = value;
        this.events = ImmutableSet.copyOf(events);
        this.children = ImmutableList.copyOf(children);
        this.style = style;
    }

    public BaseComponent(ComponentValue value, List<Component> children) {
        this(value, children, Style.INHERIT, Collections.emptySet());
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
        events.forEach(event -> event.applyToJson(object));
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
        for (Event event : events) {
            event.write(writer);
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

        if (!events.equals(that.events)) {
            return false;
        }
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
        result = 31 * result + events.hashCode();
        return result;
    }
}
