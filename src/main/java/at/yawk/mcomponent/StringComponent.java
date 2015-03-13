/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import at.yawk.mcomponent.style.Style;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * @author yawkat
 */
public class StringComponent implements Component {
    private final String value;

    public StringComponent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public Component minify() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public Optional<Component> tryJoin(Component other) {
        if (other instanceof StringComponent) {
            return Optional.of(new StringComponent(this.value + ((StringComponent) other).value));
        }
        return Optional.empty();
    }

    @Override
    public Component withStyle(Style style) {
        return style.equals(Style.INHERIT) ? this : new BaseComponent(new StringComponentValue(this.value), style);
    }

    @Override
    public String toRawString() {
        return value;
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
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

        StringComponent that = (StringComponent) o;

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
        return toJson().toString();
    }
}
