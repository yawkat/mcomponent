/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import com.google.gson.JsonObject;
import java.util.Optional;

/**
 * @author yawkat
 */
public interface ComponentValue extends JsonSerializable {
    public static ComponentValue EMPTY = new StringComponentValue("");

    void applyToJson(JsonObject target);

    default boolean isEmpty() {
        return false;
    }

    default Optional<ComponentValue> tryJoin(ComponentValue other) {
        return Optional.empty();
    }

    String toRawString();
}
