/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.List;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
public class TranslatableComponentValue implements ComponentValue {
    private final String key;
    private final List<Component> arguments;

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
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
