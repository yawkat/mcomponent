/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent.style;

import java.util.Optional;

/**
* @author yawkat
*/
public enum FlagValue {
    TRUE(Optional.of(true)),
    FALSE(Optional.of(false)),
    INHERIT(Optional.<Boolean>empty());

    private final Optional<Boolean> value;

    FlagValue(Optional<Boolean> value) {
        this.value = value;
    }

    public Optional<Boolean> getValue() {
        return value;
    }
}
