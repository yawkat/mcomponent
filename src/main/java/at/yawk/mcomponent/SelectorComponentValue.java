/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author yawkat
 */
public class SelectorComponentValue implements ComponentValue {
    private final String selector;

    public SelectorComponentValue(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    @Override
    public void applyToJson(JsonObject target) {
        target.addProperty("selector", getSelector());
    }

    @Override
    public String toRawString() {
        return "{" + selector + "}";
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.name("selector");
        writer.value(getSelector());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectorComponentValue that = (SelectorComponentValue) o;

        if (!selector.equals(that.selector)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return selector.hashCode();
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
