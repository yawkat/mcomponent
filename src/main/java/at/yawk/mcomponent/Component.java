/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import at.yawk.mcomponent.style.Style;
import com.google.gson.JsonElement;
import java.util.Optional;

/**
 * @author yawkat
 */
public interface Component extends JsonSerializable {
    JsonElement toJson();

    Component minify();

    boolean isEmpty();

    Optional<Component> tryJoin(Component other);

    Component withStyle(Style style);

    String toRawString();
}
