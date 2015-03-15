/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import at.yawk.mcomponent.action.Event;
import at.yawk.mcomponent.style.Style;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author yawkat
 */
@Getter
@EqualsAndHashCode
public class BaseComponent implements Component {
    // mutable for internal use in ComponentMinimizer

    ComponentValue value;
    List<Component> children;
    Style style;
    Set<Event> events;

    BaseComponent() {}

    public BaseComponent(ComponentValue value, List<Component> children, Style style, Set<Event> events) {
        this.value = value;
        this.events = events;
        this.children = children;
        this.style = style;
    }

    public BaseComponent(ComponentValue value, Style style) {
        this(value, new ArrayList<>(), style, new HashSet<>());
    }

    public BaseComponent(ComponentValue value, List<Component> children) {
        this(value, children, Style.INHERIT, new HashSet<>());
    }

    public BaseComponent(ComponentValue value) {
        this(value, new ArrayList<>());
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
    public boolean isEmpty() {
        return value.isEmpty() && children.stream().allMatch(Component::isEmpty);
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
        for (Event event : events) {
            event.write(writer);
        }
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
    public String toString() {
        return toJson().toString();
    }
}
