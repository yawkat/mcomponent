/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Optional;

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
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public Optional<ComponentValue> tryJoin(ComponentValue other) {
        if (other instanceof StringComponentValue) {
            return Optional.of(new StringComponentValue(getValue() + ((StringComponentValue) other).getValue()));
        }
        return Optional.empty();
    }

    @Override
    public String toRawString() {
        return value;
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

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
