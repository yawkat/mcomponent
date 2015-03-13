/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent.action;

import at.yawk.mcomponent.Component;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
public class BaseAction implements Action {
    private final Type type;
    private final Component value;

    @Override
    public String toString() {
        return type + ":'" + value + "'";
    }

    public static enum Type {
        OPEN_URL("open_url"),
        OPEN_FILE("open_file"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SHOW_ITEM("show_item"),
        INSERT("insertion"),
        ;

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
