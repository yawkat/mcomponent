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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yawkat
 */
@EqualsAndHashCode
@Getter
@Setter
public class StringComponent implements Component {
    private final String value;

    public StringComponent(String value) {
        this.value = value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
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
    public String toString() {
        return toJson().toString();
    }
}
