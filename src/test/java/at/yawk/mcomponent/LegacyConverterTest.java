/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.mcomponent;

import at.yawk.mcomponent.action.BaseAction;
import at.yawk.mcomponent.action.BaseEvent;
import at.yawk.mcomponent.style.Color;
import at.yawk.mcomponent.style.FlagKey;
import at.yawk.mcomponent.style.FlagValue;
import at.yawk.mcomponent.style.Style;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LegacyConverterTest {
    @Test
    public void testLegacyConvertBase() {
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Hi there \",\"color\":\"red\"},{\"text\":\"mate\"," +
                     "\"bold\":true,\"color\":\"blue\"},{\"text\":\"!\",\"color\":\"red\"}]}",
                     LegacyConverter.convertAndMinimize("§cHi there §9§lmate§c!").toString());
    }

    @Test
    public void testLegacyConvertLink() {
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Website: \",\"bold\":true,\"color\":\"blue\"}," +
                     "{\"text\":\"yawk.at\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\"," +
                     "\"value\":\"http://yawk.at\"}}]}",
                     LegacyConverter.convertAndMinimize("§9§lWebsite: §9yawk.at").toString());
    }

    @Test
    public void testToLegacyText() throws Exception {
        Component component = new BaseComponent(
                new StringComponentValue("Test"),
                Arrays.asList(
                        new StringComponent("String child"),
                        new BaseComponent(
                                new StringComponentValue("Base child"),
                                new Style(Color.BLUE).withFlag(FlagKey.ITALIC, FlagValue.TRUE)
                        )
                ),
                new Style(Color.RED).withFlag(FlagKey.BOLD, FlagValue.TRUE),
                Collections.singleton(
                        new BaseEvent(BaseEvent.Type.CLICK,
                                      new BaseAction(BaseAction.Type.OPEN_URL, new StringComponent("Test")))
                )
        );
        assertEquals("§c§lTestString child§9§l§oBase child", LegacyConverter.toLegacyString(component));
    }
}