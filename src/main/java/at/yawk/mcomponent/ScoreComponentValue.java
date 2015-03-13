/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
public class ScoreComponentValue implements ComponentValue {
    private final String objective;
    private final String player;

    @Override
    public void applyToJson(JsonObject target) {
        JsonObject self = new JsonObject();
        self.addProperty("objective", getObjective());
        self.addProperty("name", getPlayer());
        target.add("score", self);
    }

    @Override
    public String toRawString() {
        return "{" + objective + ":" + player + "}";
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.name("score");
        writer.beginObject();
        writer.name("objective");
        writer.value(getObjective());
        writer.name("name");
        writer.value(getPlayer());
        writer.endObject();
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        applyToJson(object);
        return object.toString();
    }
}
