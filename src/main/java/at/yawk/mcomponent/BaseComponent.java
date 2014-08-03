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
import java.util.Optional;
import java.util.Set;

/**
 * @author yawkat
 */
public class BaseComponent implements Component {
    // mutable for internal use in ComponentMinimizer

    ComponentValue value;
    List<Component> children;
    Style style;
    Set<Event> events;

    BaseComponent() {}

    public BaseComponent(ComponentValue value, List<Component> children, Style style, Set<Event> events) {
        this.value = value;
        this.events = ImmutableSet.copyOf(events);
        this.children = ImmutableList.copyOf(children);
        this.style = style;
    }

    public BaseComponent(ComponentValue value, Style style) {
        this(value, Collections.emptyList(), style, Collections.emptySet());
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
        return Collections.unmodifiableList(children);
    }

    public Style getStyle() {
        return style;
    }

    public Set<Event> getEvents() {
        return Collections.unmodifiableSet(events);
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
            && style.equals(Style.INHERIT)
            && events.isEmpty()) {
            return new StringComponent(((StringComponentValue) value).getValue());
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public Optional<Component> tryJoin(Component other) {
        if (other instanceof BaseComponent) {
            if (style.equals(((BaseComponent) other).getStyle()) &&
                children.isEmpty() &&
                ((BaseComponent) other).getChildren().isEmpty() &&
                events.equals(((BaseComponent) other).getEvents())) {
                Optional<ComponentValue> value = getValue().tryJoin(((BaseComponent) other).getValue());
                if (value.isPresent()) {
                    return Optional.of(new BaseComponent(value.get(), Collections.emptyList(), style, events));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Component withStyle(Style style) {
        BaseComponent n = new BaseComponent();
        n.value = this.value;
        n.children = this.children;
        n.events = this.events;
        n.style = style;
        return n;
    }

    @Override
    public String toRawString() {
        StringBuilder builder = new StringBuilder(this.value.toRawString());
        for (Component child : children) {
            builder.append(child.toRawString());
        }
        return builder.toString();
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        value.write(writer);
        style.write(writer);
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

    @Override
    public String toString() {
        return toJson().toString();
    }
}
