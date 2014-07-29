package at.yawk.mcomponent.action;

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

        validate();
    }

    private void validate() {
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

    @Override
    public void applyToJson(JsonObject target) {
        if (type == Type.SHIFT_CLICK) {
            target.addProperty("insertion", ((BaseAction) action).getValue());
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("action", ((BaseAction) action).getType().getId());
            object.addProperty("value", ((BaseAction) action).getValue());
            target.add(type == Type.HOVER ? "hoverEvent" : "clickEvent", object);
        }
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        if (type == Type.SHIFT_CLICK) {
            writer.name("insertion");
            writer.value(((BaseAction) action).getValue());
        } else {
            writer.name(type == Type.HOVER ? "hoverEvent" : "clickEvent");
            writer.beginObject();
            writer.name("action");
            writer.value(((BaseAction) action).getType().getId());
            writer.name("value");
            writer.value(((BaseAction) action).getValue());
            writer.endObject();
        }
    }

    public static enum Type {
        HOVER,
        CLICK,
        SHIFT_CLICK,
    }
}
