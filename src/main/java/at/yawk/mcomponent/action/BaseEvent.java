/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent.action;

import at.yawk.mcomponent.StringComponent;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public class BaseEvent implements Event {
    private final Type type;
    private final Action action;

    public BaseEvent(Type type, Action action) {
        this.type = type;
        this.action = action;

        validateActionType();
        validateActionValue();
    }

    public Type getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }

    private void validateActionType() {
        switch (type) {
        case CLICK:
            if (action instanceof BaseAction) {
                switch (((BaseAction) action).getType()) {
                case OPEN_FILE:
                case OPEN_URL:
                case RUN_COMMAND:
                case SUGGEST_COMMAND:
                    return;
                }
            }
            break;
        case HOVER:
            if (action instanceof BaseAction) {
                switch (((BaseAction) action).getType()) {
                case SHOW_TEXT:
                case SHOW_ITEM:
                case SHOW_ACHIEVEMENT:
                    return;
                }
            }
            break;
        case SHIFT_CLICK:
            if (action instanceof BaseAction) {
                switch (((BaseAction) action).getType()) {
                case INSERT:
                    return;
                }
            }
        }
        throw new IllegalArgumentException(type + " does not allow action " + action);
    }

    private void validateActionValue() {
        if (type != Type.HOVER &&
            !(action instanceof BaseAction && ((BaseAction) action).getValue() instanceof StringComponent)) {
            throw new IllegalArgumentException("Only HOVER supports non-string component values!");
        }
    }

    @Override
    public void applyToJson(JsonObject target) {
        if (type == Type.SHIFT_CLICK) {
            target.add("insertion", ((BaseAction) action).getValue().toJson());
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("action", ((BaseAction) action).getType().getId());
            object.add("value", ((BaseAction) action).getValue().toJson());
            target.add(type == Type.HOVER ? "hoverEvent" : "clickEvent", object);
        }
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        if (type == Type.SHIFT_CLICK) {
            writer.name("insertion");
            ((BaseAction) action).getValue().write(writer);
        } else {
            writer.name(type == Type.HOVER ? "hoverEvent" : "clickEvent");
            writer.beginObject();
            writer.name("action");
            writer.value(((BaseAction) action).getType().getId());
            writer.name("value");
            ((BaseAction) action).getValue().write(writer);
            writer.endObject();

        }
    }

    public static enum Type {
        HOVER,
        CLICK,
        SHIFT_CLICK,
    }
}
